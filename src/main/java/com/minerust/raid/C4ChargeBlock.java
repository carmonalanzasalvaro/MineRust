package com.minerust.raid;

import com.minerust.Config;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class C4ChargeBlock extends Block {
    public C4ChargeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        boolean anyDamageApplied = false;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            mutablePos.set(pos).move(direction);
            boolean damaged = RaidDamageHelper.applyRaidDamage(serverLevel, mutablePos, Config.C4_DAMAGE, player);
            if (damaged) {
                anyDamageApplied = true;
            }
        }

        if (anyDamageApplied) {
            player.sendSystemMessage(Component.literal("C4 detonated! Adjacent protected blocks were damaged."));
        } else {
            player.sendSystemMessage(Component.literal("C4 detonated, but no adjacent protected blocks were affected."));
        }

        level.removeBlock(pos, false);

        return InteractionResult.CONSUME;
    }
}
