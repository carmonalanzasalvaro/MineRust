package com.minerust.claim;

import com.minerust.Config;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class ToolCupboardClaimManager {

    public static void registerClaim(ServerLevel level, BlockPos tcPos, ToolCupboardBlockEntity be) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();

        ClaimSavedData.ToolCupboardData tcData = new ClaimSavedData.ToolCupboardData(
            be.getOwner(), be.getTier(), tcPos.getX(), tcPos.getZ(), dimension
        );
        tcData.setTcPackedPos(tcPos.asLong());
        for (UUID uuid : be.getAuthorizedPlayers()) {
            tcData.getAuthorizedPlayers().add(uuid);
        }

        data.addClaim(dimension, tcData);
    }

    public static boolean updateClaimTier(ServerLevel level, BlockPos tcPos, ToolCupboardBlockEntity be) {
        int newTier = be.getTier();
        if (wouldOverlap(level, tcPos, newTier, tcPos)) {
            return false;
        }
        ClaimSavedData.ToolCupboardData existingClaim = getClaimAt(level, tcPos);
        int oldTier = existingClaim != null ? existingClaim.getTier() : 1;
        removeClaim(level, tcPos, oldTier);
        registerClaim(level, tcPos, be);
        return true;
    }

    public static void removeClaim(ServerLevel level, BlockPos tcPos, int tier) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        data.removeClaim(dimension, tcPos.asLong());
    }

    public static boolean wouldOverlap(ServerLevel level, BlockPos tcPos, int tier) {
        return wouldOverlap(level, tcPos, tier, null);
    }

    public static boolean wouldOverlap(ServerLevel level, BlockPos tcPos, int tier, BlockPos ignorePos) {
        String dimension = level.dimension().location().toString();
        ClaimBounds candidateBounds = getBounds(tcPos, tier);

        ClaimSavedData data = ClaimSavedData.get(level);
        for (ClaimSavedData.ToolCupboardData claim : data.getClaims(dimension)) {
            if (ignorePos != null && claim.getTcPackedPos() == ignorePos.asLong()) {
                continue;
            }
            if (candidateBounds.overlaps(getBounds(claim))) {
                return true;
            }
        }
        return false;
    }

    public static boolean wouldOverlapMaxCoverage(ServerLevel level, BlockPos tcPos) {
        return wouldOverlapMaxCoverage(level, tcPos, null);
    }

    public static boolean wouldOverlapMaxCoverage(ServerLevel level, BlockPos tcPos, BlockPos ignorePos) {
        String dimension = level.dimension().location().toString();
        ClaimBounds candidateBounds = getBounds(tcPos, getMaxLevel());

        ClaimSavedData data = ClaimSavedData.get(level);
        for (ClaimSavedData.ToolCupboardData claim : data.getClaims(dimension)) {
            if (ignorePos != null && claim.getTcPackedPos() == ignorePos.asLong()) {
                continue;
            }
            BlockPos claimCenter = BlockPos.of(claim.getTcPackedPos());
            if (candidateBounds.overlaps(getBounds(claimCenter, getMaxLevel()))) {
                return true;
            }
        }
        return false;
    }

    public static ClaimSavedData.ToolCupboardData getClaimAt(ServerLevel level, BlockPos pos) {
        String dimension = level.dimension().location().toString();
        for (ClaimSavedData.ToolCupboardData claim : ClaimSavedData.get(level).getClaims(dimension)) {
            if (containsBlock(claim, pos)) {
                return claim;
            }
        }
        return null;
    }

    public static boolean isAuthorized(ServerLevel level, BlockPos pos, UUID playerUuid) {
        ClaimSavedData.ToolCupboardData claim = getClaimAt(level, pos);
        if (claim == null) {
            return true;
        }

        if (playerUuid.equals(claim.getOwner())) {
            return true;
        }

        return claim.getAuthorizedPlayers().contains(playerUuid);
    }

    public static int getMinLevel() {
        return Config.TC_MIN_LEVEL > 0 ? Config.TC_MIN_LEVEL : 1;
    }

    public static int getMaxLevel() {
        int configuredMax = Config.TC_MAX_LEVEL > 0 ? Config.TC_MAX_LEVEL : 20;
        return Math.max(getMinLevel(), configuredMax);
    }

    public static int clampLevel(int level) {
        return Math.max(getMinLevel(), Math.min(getMaxLevel(), level));
    }

    public static int getFootprintSize(int level) {
        int minLevel = getMinLevel();
        int maxLevel = getMaxLevel();
        int clampedLevel = clampLevel(level);
        int baseFootprint = Math.max(1, Config.TC_BASE_FOOTPRINT > 0 ? Config.TC_BASE_FOOTPRINT : 10);
        int maxFootprint = Math.max(baseFootprint, Config.TC_MAX_FOOTPRINT > 0 ? Config.TC_MAX_FOOTPRINT : 30);
        if (maxLevel == minLevel) {
            return maxFootprint;
        }
        int levelOffset = clampedLevel - minLevel;
        int levelRange = maxLevel - minLevel;
        int footprintRange = maxFootprint - baseFootprint;
        return baseFootprint + (levelOffset * footprintRange) / levelRange;
    }

    public static int getVerticalRadius() {
        return Math.max(0, Config.TC_VERTICAL_RADIUS > 0 ? Config.TC_VERTICAL_RADIUS : 30);
    }

    public static ClaimBounds getBounds(ClaimSavedData.ToolCupboardData claim) {
        return getBounds(BlockPos.of(claim.getTcPackedPos()), claim.getTier());
    }

    public static ClaimBounds getBounds(BlockPos center, int tier) {
        int footprint = getFootprintSize(tier);
        int negativeReach = (footprint - 1) / 2;
        int positiveReach = footprint - negativeReach - 1;
        int verticalRadius = getVerticalRadius();
        int downwardReach = verticalRadius;
        int upwardReach = Math.max(0, verticalRadius - 1);
        return new ClaimBounds(
            center.getX() - negativeReach,
            center.getY() - downwardReach,
            center.getZ() - negativeReach,
            center.getX() + positiveReach,
            center.getY() + upwardReach,
            center.getZ() + positiveReach
        );
    }

    public static boolean containsBlock(ClaimSavedData.ToolCupboardData claim, BlockPos pos) {
        return getBounds(claim).contains(pos);
    }

    public record ClaimBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        public boolean contains(BlockPos pos) {
            return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getY() >= minY && pos.getY() <= maxY
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }

        public boolean overlaps(ClaimBounds other) {
            return minX <= other.maxX && maxX >= other.minX
                && minY <= other.maxY && maxY >= other.minY
                && minZ <= other.maxZ && maxZ >= other.minZ;
        }

        public int sizeX() {
            return maxX - minX + 1;
        }

        public int sizeY() {
            return maxY - minY + 1;
        }

        public int sizeZ() {
            return maxZ - minZ + 1;
        }
    }

}
