package com.minerust.menu;

import com.minerust.claim.ToolCupboardBlock;
import com.minerust.claim.ToolCupboardBlockEntity;
import com.minerust.registry.ModBlocks;
import com.minerust.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;

public class SecurityPanelMenu extends AbstractContainerMenu {
    public static final int BUTTON_UPGRADE = 0;
    public static final int BUTTON_SHOW_BOUNDS = 1;
    public static final int BUTTON_AUTHORIZE_NEARBY = 2;

    private final BlockPos pos;
    private final DataSlot tierData;
    private int syncedTier = 1;

    public SecurityPanelMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ModMenus.SECURITY_PANEL.get(), containerId);
        this.pos = pos;
        this.tierData = new DataSlot() {
            @Override
            public int get() {
                if (playerInventory.player.level().isClientSide) {
                    return syncedTier;
                }
                if (playerInventory.player.level().getBlockEntity(pos) instanceof ToolCupboardBlockEntity be) {
                    syncedTier = be.getTier();
                    return be.getTier();
                }
                return syncedTier;
            }

            @Override
            public void set(int value) {
                syncedTier = value;
            }
        };
        addDataSlot(tierData);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getTier() {
        return tierData.get();
    }

    public void setSyncedTier(int tier) {
        this.syncedTier = tier;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be)) {
            return false;
        }
        if (!player.getUUID().equals(be.getOwner())) {
            return false;
        }

        if (id == BUTTON_UPGRADE) {
            return ToolCupboardBlock.upgradeSecurityPanel(level, pos, player, be);
        }
        if (id == BUTTON_SHOW_BOUNDS) {
            ToolCupboardBlock.toggleClaimBounds(level, player, be, pos);
            return true;
        }
        if (id == BUTTON_AUTHORIZE_NEARBY) {
            return ToolCupboardBlock.authorizeNearbyPlayer(level, player, be, pos);
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.level().getBlockState(pos).is(ModBlocks.TOOL_CUPBOARD.get())
                && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
}
