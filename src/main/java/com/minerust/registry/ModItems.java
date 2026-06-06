package com.minerust.registry;

import com.minerust.MineRustMod;
import com.minerust.item.ClaimDebugStickItem;
import com.minerust.combat.ScrapPistolItem;
import com.minerust.item.ProtectionStaffItem;
import com.minerust.raid.RaidDrillItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MineRustMod.MODID);

    public static final RegistryObject<Item> TOOL_CUPBOARD = ITEMS.register("tool_cupboard",
            () -> new BlockItem(ModBlocks.TOOL_CUPBOARD.get(), new Item.Properties()));

    public static final RegistryObject<Item> SLEEPING_BAG = ITEMS.register("sleeping_bag",
            () -> new BlockItem(ModBlocks.SLEEPING_BAG.get(), new Item.Properties()));

    public static final RegistryObject<Item> C4_CHARGE = ITEMS.register("c4_charge",
            () -> new BlockItem(ModBlocks.C4_CHARGE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PROTECTION_STAFF = ITEMS.register("protection_staff",
            () -> new ProtectionStaffItem(new Item.Properties()));

    public static final RegistryObject<Item> CLAIM_DEBUG_STICK = ITEMS.register("claim_debug_stick",
            () -> new ClaimDebugStickItem(new Item.Properties()));

    public static final RegistryObject<Item> RAID_DRILL = ITEMS.register("raid_drill",
            () -> new RaidDrillItem(new Item.Properties()));

    public static final RegistryObject<Item> SCRAP_PISTOL = ITEMS.register("scrap_pistol",
            () -> new ScrapPistolItem(new Item.Properties()));

    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
