package com.minerust.raid;

import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

/**
 * Reusable helper for applying MineRust raid damage to protected blocks.
 * Used by C4 charges and raid drills.
 */
public class RaidDamageHelper {

    /**
     * Applies raid damage to a protected block at the given position.
     * If the block is not protected, no action is taken.
     *
     * @param level  the server level
     * @param pos    the block position to damage
     * @param damage the amount of damage to apply
     * @param player the player responsible for the damage (used for drops)
     * @return true if damage was applied to a protected block, false otherwise
     */
    public static boolean applyRaidDamage(ServerLevel level, BlockPos pos, int damage, Player player) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        Map<Long, ClaimSavedData.ProtectedBlockData> dimBlocks = data.getProtectedBlocksByDimension().get(dimension);

        if (dimBlocks == null) {
            return false;
        }

        ClaimSavedData.ProtectedBlockData blockData = dimBlocks.get(pos.asLong());
        if (blockData == null) {
            return false;
        }

        int newHealth = blockData.getCurrentHealth() - damage;
        if (newHealth <= 0) {
            BlockState state = level.getBlockState(pos);
            Block.dropResources(state, level, pos, null, player, ItemStack.EMPTY);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            data.removeProtectedBlock(dimension, pos);
        } else {
            blockData.setCurrentHealth(newHealth);
            data.setDirty();
        }

        return true;
    }
}
