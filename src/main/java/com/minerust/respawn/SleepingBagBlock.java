package com.minerust.respawn;

import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SleepingBagBlock extends Block {
    public SleepingBagBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level instanceof ServerLevel serverLevel && placer instanceof Player player) {
            ClaimSavedData data = ClaimSavedData.get(serverLevel);
            ClaimSavedData.PlayerCooldownData cooldownData = data.getPlayerCooldowns().computeIfAbsent(
                player.getUUID(),
                uuid -> new ClaimSavedData.PlayerCooldownData(uuid)
            );
            cooldownData.setSleepingBagDimension(serverLevel.dimension().location().toString());
            cooldownData.setSleepingBagPos(pos.asLong());
            data.setPlayerCooldown(player.getUUID(), cooldownData);
        }
    }
}
