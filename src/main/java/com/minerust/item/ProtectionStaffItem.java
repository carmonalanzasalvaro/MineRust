package com.minerust.item;

import com.minerust.blockhealth.ProtectionTier;
import com.minerust.claim.ToolCupboardClaimManager;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ProtectionStaffItem extends Item {
    private static final String TIER_TAG = "SelectedTier";

    public ProtectionStaffItem(Properties properties) {
        super(properties);
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

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();

        if (player.isShiftKeyDown()) {
            return cycleTier(stack, player);
        } else {
            return applyProtection((ServerLevel) level, pos, stack, player);
        }
    }

    private InteractionResult cycleTier(ItemStack stack, Player player) {
        ProtectionTier current = getSelectedTier(stack);
        ProtectionTier next = current.next();
        stack.getOrCreateTag().putString(TIER_TAG, next.name());
        player.displayClientMessage(
            Component.literal("Protection tier: " + next.name()),
            true
        );
        return InteractionResult.SUCCESS;
    }

    private InteractionResult applyProtection(ServerLevel level, BlockPos pos, ItemStack stack, Player player) {
        String dimension = level.dimension().location().toString();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        ClaimSavedData data = ClaimSavedData.get(level);
        ClaimSavedData.ToolCupboardData claim = data.getClaimAtChunk(dimension, chunkX, chunkZ);

        if (level.getBlockState(pos).getDestroySpeed(level, pos) < 0) {
            player.displayClientMessage(
                Component.literal("This block cannot be protected."),
                true
            );
            return InteractionResult.FAIL;
        }

        if (claim == null) {
            player.displayClientMessage(
                Component.literal("No Tool Cupboard claim covers this block."),
                true
            );
            return InteractionResult.FAIL;
        }

        if (!ToolCupboardClaimManager.isAuthorized(level, pos, player.getUUID())) {
            player.displayClientMessage(
                Component.literal("You must be the owner or an authorized player for this claim."),
                true
            );
            return InteractionResult.FAIL;
        }

        ProtectionTier tier = getSelectedTier(stack);

        if (!player.isCreative()) {
            boolean hasMaterial = false;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == tier.getMaterialItem()) {
                    hasMaterial = true;
                    break;
                }
            }
            if (!hasMaterial) {
                player.displayClientMessage(
                    Component.literal("You need " + tier.getMaterialItem().getDescription().getString() + " to apply " + tier.name() + " protection."),
                    true
                );
                return InteractionResult.FAIL;
            }
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == tier.getMaterialItem()) {
                    invStack.shrink(1);
                    break;
                }
            }
        }

        ClaimSavedData.ProtectedBlockData blockData = new ClaimSavedData.ProtectedBlockData(
            pos.asLong(),
            dimension,
            tier.name(),
            tier.getMaxHealth(),
            player.getUUID()
        );

        data.addProtectedBlock(dimension, pos, blockData);

        player.displayClientMessage(
            Component.literal(tier.name() + " protection applied."),
            true
        );

        return InteractionResult.SUCCESS;
    }

    private ProtectionTier getSelectedTier(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains(TIER_TAG)) {
            return ProtectionTier.fromString(stack.getTag().getString(TIER_TAG));
        }
        return ProtectionTier.STRAW;
    }
}
