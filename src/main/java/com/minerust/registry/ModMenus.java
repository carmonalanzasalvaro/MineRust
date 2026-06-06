package com.minerust.registry;

import com.minerust.MineRustMod;
import com.minerust.menu.SecurityPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MineRustMod.MODID);

    public static final RegistryObject<MenuType<SecurityPanelMenu>> SECURITY_PANEL = MENUS.register("security_panel",
            () -> IForgeMenuType.create((windowId, inv, data) -> new SecurityPanelMenu(windowId, inv, data.readBlockPos())));

    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
