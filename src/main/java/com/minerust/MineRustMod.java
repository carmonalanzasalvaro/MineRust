package com.minerust;

import com.minerust.registry.ModBlockEntities;
import com.minerust.registry.ModBlocks;
import com.minerust.registry.ModCreativeTabs;
import com.minerust.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * MineRust - A Rust-inspired survival mod for Minecraft 1.20.1 (Forge).
 *
 * Target systems (implement one at a time):
 *  - Land claims and team management.
 *  - Raid windows and base raiding.
 *  - Block health / destructible structures.
 *  - PvP-oriented crafting and economy.
 */
@Mod(MineRustMod.MODID)
public class MineRustMod {

    // Unique mod identifier. Must match the entry in META-INF/mods.toml.
    public static final String MODID = "minerust";

    private static final Logger LOGGER = LogUtils.getLogger();

    public MineRustMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.init(modEventBus);
        ModItems.init(modEventBus);
        ModBlockEntities.init(modEventBus);
        ModCreativeTabs.init(modEventBus);

        // Register common setup listener
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);

        // Register common ForgeConfigSpec so Forge creates the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("MineRust common setup complete.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("MineRust server starting: {}", event.getServer().getWorldData().getLevelName());
    }
}
