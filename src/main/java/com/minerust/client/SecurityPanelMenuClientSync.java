package com.minerust.client;

import com.minerust.menu.SecurityPanelMenu;
import net.minecraft.client.Minecraft;

public class SecurityPanelMenuClientSync {
    public static void applyTier(int tier) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof SecurityPanelMenu menu) {
            menu.setSyncedTier(tier);
        }
    }
}
