package com.minerust.raid;

import com.minerust.Config;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Map;

public class RaidDrillItem extends Item {
    private static final int DEFAULT_DURABILITY = 256;
    private static final int USE_DURATION = 72000;
    private static final String TAG_POS_X = "DrillPosX";
    private static final String TAG_POS_Y = "DrillPosY";
    private static final String TAG_POS_Z = "DrillPosZ";
    private static final String TAG_LAST_TICK = "DrillLastTick";

    public RaidDrillItem(Properties properties) {
        super(properties.durability(DEFAULT_DURABILITY));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }

        BlockPos pos = context.getClickedPos();
        ServerLevel serverLevel = (ServerLevel) level;

        if (!isProtected(serverLevel, pos)) {
            player.displayClientMessage(
                Component.literal("This block is not protected."),
                true
            );
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_POS_X, pos.getX());
        tag.putInt(TAG_POS_Y, pos.getY());
        tag.putInt(TAG_POS_Z, pos.getZ());
        tag.putLong(TAG_LAST_TICK, serverLevel.getGameTime());

        player.startUsingItem(context.getHand());
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            player.stopUsingItem();
            return;
        }

        int x = tag.getInt(TAG_POS_X);
        int y = tag.getInt(TAG_POS_Y);
        int z = tag.getInt(TAG_POS_Z);
        BlockPos pos = new BlockPos(x, y, z);

        ServerLevel serverLevel = (ServerLevel) level;

        if (!serverLevel.isLoaded(pos) || !isProtected(serverLevel, pos)) {
            player.stopUsingItem();
            return;
        }

        long gameTime = serverLevel.getGameTime();
        long lastTick = tag.getLong(TAG_LAST_TICK);
        if (gameTime - lastTick < 20) {
            return;
        }
        tag.putLong(TAG_LAST_TICK, gameTime);

        boolean damaged = RaidDamageHelper.applyRaidDamage(
            serverLevel,
            pos,
            Config.DRILL_DAMAGE_PER_SECOND,
            player
        );

        if (!damaged) {
            player.stopUsingItem();
            return;
        }

        int cost = Config.DRILL_DURABILITY_COST;
        stack.hurt(cost, player.getRandom(), player);

        if (stack.isEmpty()) {
            player.stopUsingItem();
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!level.isClientSide && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            tag.remove(TAG_POS_X);
            tag.remove(TAG_POS_Y);
            tag.remove(TAG_POS_Z);
            tag.remove(TAG_LAST_TICK);
        }
    }

    private static boolean isProtected(ServerLevel level, BlockPos pos) {
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        Map<Long, ClaimSavedData.ProtectedBlockData> dimBlocks = data.getProtectedBlocksByDimension().get(dimension);

        if (dimBlocks == null) {
            return false;
        }

        return dimBlocks.containsKey(pos.asLong());
    }
}
