package com.minerust.claim;

import com.minerust.Config;
import com.minerust.MineRustMod;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MineRustMod.MODID)
public class ToolCupboardUpkeepManager {

    private static final int CHECK_INTERVAL_TICKS = 1200;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.getServer() == null) {
            return;
        }

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;

        ClaimSavedData data = ClaimSavedData.get(event.getServer());
        long now = System.currentTimeMillis();
        long intervalMs = (long) Config.DECAY_INTERVAL_HOURS * 3600000L;
        if (now - data.getLastUpkeepTimestamp() < intervalMs) {
            return;
        }

        data.setLastUpkeepTimestamp(now);
        processUpkeep(event.getServer());
    }

    private static void processUpkeep(MinecraftServer server) {
        ClaimSavedData data = ClaimSavedData.get(server);

        for (Map.Entry<String, Map<Long, ClaimSavedData.ToolCupboardData>> dimEntry : data.getClaimsByDimension().entrySet()) {
            String dimension = dimEntry.getKey();
            ResourceLocation rl = ResourceLocation.tryParse(dimension);
            if (rl == null) {
                continue;
            }
            ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, rl));
            if (level == null) {
                continue;
            }

            Map<Long, List<ClaimSavedData.ProtectedBlockData>> blocksByChunk = groupProtectedBlocksByChunk(data, dimension);

            for (ClaimSavedData.ToolCupboardData tcData : dimEntry.getValue().values()) {
                processToolCupboard(level, data, tcData, blocksByChunk);
            }
        }
    }

    private static Map<Long, List<ClaimSavedData.ProtectedBlockData>> groupProtectedBlocksByChunk(ClaimSavedData data, String dimension) {
        Map<Long, List<ClaimSavedData.ProtectedBlockData>> result = new HashMap<>();
        Map<Long, ClaimSavedData.ProtectedBlockData> dimBlocks = data.getProtectedBlocksByDimension().get(dimension);
        if (dimBlocks == null) {
            return result;
        }

        for (ClaimSavedData.ProtectedBlockData block : dimBlocks.values()) {
            BlockPos pos = BlockPos.of(block.getPackedPos());
            long packedChunk = packChunk(pos.getX() >> 4, pos.getZ() >> 4);
            result.computeIfAbsent(packedChunk, k -> new ArrayList<>()).add(block);
        }
        return result;
    }

    private static void processToolCupboard(ServerLevel level, ClaimSavedData data, ClaimSavedData.ToolCupboardData tcData, Map<Long, List<ClaimSavedData.ProtectedBlockData>> blocksByChunk) {
        int radius = tcData.getTier() == 1 ? Config.TC_TIER1_CHUNK_RADIUS : Config.TC_TIER2_CHUNK_RADIUS;

        int totalWood = 0;
        int totalStone = 0;
        int totalMetal = 0;
        int totalHq = 0;
        List<ClaimSavedData.ProtectedBlockData> affectedBlocks = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                long packedChunk = packChunk(tcData.getChunkX() + dx, tcData.getChunkZ() + dz);
                List<ClaimSavedData.ProtectedBlockData> chunkBlocks = blocksByChunk.get(packedChunk);
                if (chunkBlocks == null) {
                    continue;
                }

                for (ClaimSavedData.ProtectedBlockData block : chunkBlocks) {
                    affectedBlocks.add(block);
                    String tier = block.getTier();
                    if ("STRAW".equals(tier)) {
                        totalWood += Config.UPKEEP_STRAW;
                    } else if ("WOOD".equals(tier)) {
                        totalWood += Config.UPKEEP_WOOD;
                    } else if ("STONE".equals(tier)) {
                        totalStone += Config.UPKEEP_STONE;
                    } else if ("METAL".equals(tier)) {
                        totalMetal += Config.UPKEEP_METAL;
                    } else if ("HQ".equals(tier)) {
                        totalHq += Config.UPKEEP_HQ;
                    }
                }
            }
        }

        BlockPos tcBlockPos = BlockPos.of(tcData.getTcPackedPos());
        BlockEntity be = level.getBlockEntity(tcBlockPos);
        if (!(be instanceof ToolCupboardBlockEntity tcBe)) {
            decayBlocks(data, affectedBlocks, level);
            return;
        }

        boolean paid = tcBe.tryConsume(totalWood, totalStone, totalMetal, totalHq);
        if (!paid) {
            decayBlocks(data, affectedBlocks, level);
        }
    }

    private static void decayBlocks(ClaimSavedData data, List<ClaimSavedData.ProtectedBlockData> blocks, ServerLevel level) {
        String dimension = level.dimension().location().toString();
        for (ClaimSavedData.ProtectedBlockData block : blocks) {
            String tier = block.getTier();
            if ("STRAW".equals(tier)) {
                int newHealth = block.getCurrentHealth() - (block.getMaxHealth() / 2);
                if (newHealth <= 0) {
                    data.removeProtectedBlock(dimension, BlockPos.of(block.getPackedPos()));
                } else {
                    block.setCurrentHealth(newHealth);
                    data.setDirty();
                }
            } else {
                String newTier;
                if ("HQ".equals(tier)) {
                    newTier = "METAL";
                } else if ("METAL".equals(tier)) {
                    newTier = "STONE";
                } else if ("STONE".equals(tier)) {
                    newTier = "WOOD";
                } else if ("WOOD".equals(tier)) {
                    newTier = "STRAW";
                } else {
                    newTier = "STRAW";
                }
                block.setTier(newTier);
                block.setCurrentHealth(block.getMaxHealth());
                data.setDirty();
            }
        }
    }

    private static long packChunk(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
