package com.minerust.client;

import com.minerust.menu.SecurityPanelMenu;
import com.minerust.claim.ToolCupboardBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SecurityPanelScreen extends AbstractContainerScreen<SecurityPanelMenu> {
    public SecurityPanelScreen(SecurityPanelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 118;
        inventoryLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = leftPos + 22;
        int buttonY = topPos + 34;

        addRenderableWidget(Button.builder(Component.literal("Upgrade Level"), button -> sendButton(SecurityPanelMenu.BUTTON_UPGRADE))
                .bounds(buttonX, buttonY, 132, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Show/Hide Claim Bounds"), button -> sendButton(SecurityPanelMenu.BUTTON_SHOW_BOUNDS))
                .bounds(buttonX, buttonY + 24, 132, 20)
                .build());
        addRenderableWidget(Button.builder(Component.literal("Authorize Nearby"), button -> sendButton(SecurityPanelMenu.BUTTON_AUTHORIZE_NEARBY))
                .bounds(buttonX, buttonY + 48, 132, 20)
                .build());
    }

    private void sendButton(int buttonId) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xD0101010);
        guiGraphics.fill(leftPos + 4, topPos + 4, leftPos + imageWidth - 4, topPos + imageHeight - 4, 0xFF2A2A2A);
        guiGraphics.fill(leftPos + 8, topPos + 8, leftPos + imageWidth - 8, topPos + imageHeight - 8, 0xFF111820);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0xE0E0E0, false);
        guiGraphics.drawString(font, Component.literal(ToolCupboardBlock.describeLevel(menu.getTier())), 22, 16, 0xA0E0A0, false);
        guiGraphics.drawString(font, Component.literal("Area: " + ToolCupboardBlock.describeArea(menu.getTier())), 22, 26, 0xA0E0A0, false);
        guiGraphics.drawString(font, Component.literal("Next: " + ToolCupboardBlock.describeNextUpgradeCost(menu.getTier())), 22, 106, 0xE0C080, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
