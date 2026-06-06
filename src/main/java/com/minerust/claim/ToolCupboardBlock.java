package com.minerust.claim;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class ToolCupboardBlock extends BaseEntityBlock {
    private final int tier;

    public ToolCupboardBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ToolCupboardBlockEntity be = new ToolCupboardBlockEntity(pos, state);
        be.setTier(tier);
        return be;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            if (level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be) {
                be.setOwner(player.getUUID());
                ToolCupboardClaimManager.registerClaim((ServerLevel) level, pos, be);
                be.setChanged();
                ((ServerLevel) level).sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be)) {
            return InteractionResult.PASS;
        }
        if (!player.getUUID().equals(be.getOwner())) {
            player.displayClientMessage(Component.literal("You are not the owner of this Tool Cupboard."), true);
            return InteractionResult.CONSUME;
        }
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        UUID targetUuid = null;
        String targetName = null;
        double bestDistSq = Double.MAX_VALUE;
        double range = 5.0;

        for (ServerPlayer other : serverLevel.players()) {
            if (other.getUUID().equals(player.getUUID())) {
                continue;
            }
            if (be.getAuthorizedPlayers().contains(other.getUUID())) {
                continue;
            }
            double distSq = other.distanceToSqr(player.getX(), player.getY(), player.getZ());
            if (distSq <= range * range && distSq < bestDistSq) {
                bestDistSq = distSq;
                targetUuid = other.getUUID();
                targetName = other.getName().getString();
            }
        }

        if (targetUuid == null) {
            player.displayClientMessage(Component.literal("No unauthorized players nearby to authorize."), true);
            return InteractionResult.CONSUME;
        }

        be.addAuthorizedPlayer(targetUuid);
        ToolCupboardClaimManager.registerClaim(serverLevel, pos, be);

        player.displayClientMessage(Component.literal("Authorized " + targetName + "."), true);

        return InteractionResult.CONSUME;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
