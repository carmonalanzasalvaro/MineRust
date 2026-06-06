package com.minerust.registry;

import com.minerust.MineRustMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MineRustMod.MODID);

    public static final RegistryObject<CreativeModeTab> MINERUST_TAB = CREATIVE_MODE_TABS.register("minerust_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.SCRAP_PISTOL.get()))
                    .title(Component.translatable("itemGroup.minerust"))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.TOOL_CUPBOARD.get());
                        output.accept(ModItems.SLEEPING_BAG.get());
                        output.accept(ModItems.C4_CHARGE.get());
                        output.accept(ModItems.PROTECTION_STAFF.get());
                        output.accept(ModItems.CLAIM_DEBUG_STICK.get());
                        output.accept(ModItems.RAID_DRILL.get());
                        output.accept(ModItems.SCRAP_PISTOL.get());
                    })
                    .build());

    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
