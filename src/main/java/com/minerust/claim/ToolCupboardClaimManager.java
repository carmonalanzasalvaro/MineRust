package com.minerust.claim;

import com.minerust.Config;
import com.minerust.MineRustMod;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class ToolCupboardClaimManager {

    public static void registerClaim(ServerLevel level, BlockPos tcPos, ToolCupboardBlockEntity be) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        int centerChunkX = tcPos.getX() >> 4;
        int centerChunkZ = tcPos.getZ() >> 4;
        int radius = be.getTier() == 1 ? Config.TC_TIER1_CHUNK_RADIUS : Config.TC_TIER2_CHUNK_RADIUS;

        ClaimSavedData.ToolCupboardData tcData = new ClaimSavedData.ToolCupboardData(
            be.getOwner(), be.getTier(), centerChunkX, centerChunkZ, dimension
        );
        tcData.setTcPackedPos(tcPos.asLong());
        for (UUID uuid : be.getAuthorizedPlayers()) {
            tcData.getAuthorizedPlayers().add(uuid);
        }

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                data.addClaim(dimension, centerChunkX + dx, centerChunkZ + dz, tcData);
            }
        }
    }

    public static void removeClaim(ServerLevel level, BlockPos tcPos, int tier) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        int centerChunkX = tcPos.getX() >> 4;
        int centerChunkZ = tcPos.getZ() >> 4;
        int radius = tier == 1 ? Config.TC_TIER1_CHUNK_RADIUS : Config.TC_TIER2_CHUNK_RADIUS;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                data.removeClaim(dimension, centerChunkX + dx, centerChunkZ + dz);
            }
        }
    }

    public static boolean wouldOverlap(ServerLevel level, BlockPos tcPos, int tier) {
        return wouldOverlap(level, tcPos, tier, null);
    }

    public static boolean wouldOverlap(ServerLevel level, BlockPos tcPos, int tier, BlockPos ignorePos) {
        String dimension = level.dimension().location().toString();
        int centerChunkX = tcPos.getX() >> 4;
        int centerChunkZ = tcPos.getZ() >> 4;
        int radius = tier == 1 ? Config.TC_TIER1_CHUNK_RADIUS : Config.TC_TIER2_CHUNK_RADIUS;

        ClaimSavedData data = ClaimSavedData.get(level);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ClaimSavedData.ToolCupboardData claim = data.getClaimAtChunk(dimension, centerChunkX + dx, centerChunkZ + dz);
                if (claim != null) {
                    if (ignorePos != null && claim.getTcPackedPos() == ignorePos.asLong()) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAuthorized(ServerLevel level, BlockPos pos, UUID playerUuid) {
        String dimension = level.dimension().location().toString();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        ClaimSavedData data = ClaimSavedData.get(level);
        ClaimSavedData.ToolCupboardData claim = data.getClaimAtChunk(dimension, chunkX, chunkZ);
        if (claim == null) {
            return true;
        }

        if (playerUuid.equals(claim.getOwner())) {
            return true;
        }

        return claim.getAuthorizedPlayers().contains(playerUuid);
    }
}
