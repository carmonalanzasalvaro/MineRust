package com.minerust.item;

import com.minerust.Config;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

public class ClaimDebugStickItem extends Item {
    private static final int SCAN_RADIUS_CHUNKS = 5;

    public ClaimDebugStickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        String dimension = serverLevel.dimension().location().toString();
        int currentChunkX = player.blockPosition().getX() >> 4;
        int currentChunkZ = player.blockPosition().getZ() >> 4;

        ClaimSavedData data = ClaimSavedData.get(serverLevel);
        ClaimSavedData.ToolCupboardData claim = data.getClaimAtChunk(dimension, currentChunkX, currentChunkZ);

        if (claim == null) {
            player.displayClientMessage(Component.literal("No Tool Cupboard claim covers this chunk."), false);
        } else {
            int radius = getClaimRadius(claim.getTier());
            int minChunkX = claim.getChunkX() - radius;
            int minChunkZ = claim.getChunkZ() - radius;
            int maxChunkX = claim.getChunkX() + radius;
            int maxChunkZ = claim.getChunkZ() + radius;

            player.displayClientMessage(Component.literal("Tool Cupboard claim found:"), false);
            player.displayClientMessage(Component.literal("Owner UUID: " + claim.getOwner()), false);
            player.displayClientMessage(Component.literal("Tier: " + claim.getTier()), false);
            player.displayClientMessage(Component.literal("TC center chunk: " + claim.getChunkX() + ", " + claim.getChunkZ()), false);
            player.displayClientMessage(Component.literal("Current chunk: " + currentChunkX + ", " + currentChunkZ), false);
            player.displayClientMessage(Component.literal("Covered chunks: " + minChunkX + ", " + minChunkZ + " -> " + maxChunkX + ", " + maxChunkZ), false);
        }

        outlineNearbyClaims(serverLevel, player, dimension);
        return InteractionResultHolder.success(stack);
    }

    private static void outlineNearbyClaims(ServerLevel level, Player player, String dimension) {
        ClaimSavedData data = ClaimSavedData.get(level);
        int playerChunkX = player.blockPosition().getX() >> 4;
        int playerChunkZ = player.blockPosition().getZ() >> 4;
        double particleY = player.getY() + 1.0D;

        for (int dx = -SCAN_RADIUS_CHUNKS; dx <= SCAN_RADIUS_CHUNKS; dx++) {
            for (int dz = -SCAN_RADIUS_CHUNKS; dz <= SCAN_RADIUS_CHUNKS; dz++) {
                int chunkX = playerChunkX + dx;
                int chunkZ = playerChunkZ + dz;
                if (data.getClaimAtChunk(dimension, chunkX, chunkZ) == null) {
                    continue;
                }

                outlineChunk(level, chunkX, chunkZ, particleY);
            }
        }
    }

    private static void outlineChunk(ServerLevel level, int chunkX, int chunkZ, double y) {
        double minX = chunkX * 16.0D;
        double minZ = chunkZ * 16.0D;
        double maxX = minX + 16.0D;
        double maxZ = minZ + 16.0D;

        for (int offset = 0; offset <= 16; offset += 2) {
            double x = minX + offset;
            double z = minZ + offset;

            for (double h = 0; h < 3; h += 1.0D) {
                level.sendParticles(ParticleTypes.END_ROD, x, y + h, minZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                level.sendParticles(ParticleTypes.END_ROD, x, y + h, maxZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                level.sendParticles(ParticleTypes.END_ROD, minX, y + h, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                level.sendParticles(ParticleTypes.END_ROD, maxX, y + h, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static int getClaimRadius(int tier) {
        return tier == 1 ? Config.TC_TIER1_CHUNK_RADIUS : Config.TC_TIER2_CHUNK_RADIUS;
    }
}
