package com.minerust;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MineRustMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue TC_MIN_LEVEL_CONFIG = BUILDER
            .comment("Starting Security Panel level.")
            .defineInRange("tcMinLevel", 1, 1, 100);

    private static final ForgeConfigSpec.IntValue TC_MAX_LEVEL_CONFIG = BUILDER
            .comment("Maximum Security Panel level.")
            .defineInRange("tcMaxLevel", 20, 1, 100);

    private static final ForgeConfigSpec.IntValue TC_BASE_FOOTPRINT_CONFIG = BUILDER
            .comment("Horizontal footprint size in blocks at the starting level.")
            .defineInRange("tcBaseFootprint", 10, 1, 256);

    private static final ForgeConfigSpec.IntValue TC_MAX_FOOTPRINT_CONFIG = BUILDER
            .comment("Horizontal footprint size in blocks at the maximum level.")
            .defineInRange("tcMaxFootprint", 30, 1, 512);

    private static final ForgeConfigSpec.IntValue TC_VERTICAL_RADIUS_CONFIG = BUILDER
            .comment("Vertical coverage radius in blocks above and below the Security Panel.")
            .defineInRange("tcVerticalRadius", 30, 0, 320);

    // Upkeep costs per protected block tier
    private static final ForgeConfigSpec.IntValue UPKEEP_STRAW_CONFIG = BUILDER
            .comment("Upkeep resource cost per protected block for STRAW tier.")
            .defineInRange("upkeepStraw", 0, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UPKEEP_WOOD_CONFIG = BUILDER
            .comment("Upkeep resource cost per protected block for WOOD tier.")
            .defineInRange("upkeepWood", 1, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UPKEEP_STONE_CONFIG = BUILDER
            .comment("Upkeep resource cost per protected block for STONE tier.")
            .defineInRange("upkeepStone", 2, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UPKEEP_METAL_CONFIG = BUILDER
            .comment("Upkeep resource cost per protected block for METAL tier.")
            .defineInRange("upkeepMetal", 4, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue UPKEEP_HQ_CONFIG = BUILDER
            .comment("Upkeep resource cost per protected block for HQ tier.")
            .defineInRange("upkeepHq", 8, 0, Integer.MAX_VALUE);

    // Decay
    private static final ForgeConfigSpec.IntValue DECAY_INTERVAL_HOURS_CONFIG = BUILDER
            .comment("Hours between decay ticks when upkeep is unpaid.")
            .defineInRange("decayIntervalHours", 24, 1, Integer.MAX_VALUE);

    // Raid / damage
    private static final ForgeConfigSpec.IntValue C4_DAMAGE_CONFIG = BUILDER
            .comment("Damage dealt by a single C4 charge to protected blocks.")
            .defineInRange("c4Damage", 100, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DRILL_DAMAGE_PER_SECOND_CONFIG = BUILDER
            .comment("Damage per second dealt by the raid drill to protected blocks.")
            .defineInRange("drillDamagePerSecond", 5, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DRILL_DURABILITY_COST_CONFIG = BUILDER
            .comment("Durability cost per second while the raid drill is active.")
            .defineInRange("drillDurabilityCost", 1, 0, Integer.MAX_VALUE);

    // Weapon balance
    private static final ForgeConfigSpec.DoubleValue WEAPON_DAMAGE_CONFIG = BUILDER
            .comment("Base damage for MineRust firearms.")
            .defineInRange("weaponDamage", 6.0, 0.0, 100.0);

    private static final ForgeConfigSpec.IntValue WEAPON_COOLDOWN_TICKS_CONFIG = BUILDER
            .comment("Cooldown in ticks between MineRust firearm shots.")
            .defineInRange("weaponCooldownTicks", 10, 0, 200);

    // PvP
    private static final ForgeConfigSpec.IntValue PVP_SLEEPING_BAG_COOLDOWN_SECONDS_CONFIG = BUILDER
            .comment("Cooldown in seconds before a player can respawn at the same sleeping bag after PvP death.")
            .defineInRange("pvpSleepingBagCooldownSeconds", 60, 0, 3600);

    private static final ForgeConfigSpec.BooleanValue DIRECT_VANILLA_PVP_DAMAGE_FILTER_CONFIG = BUILDER
            .comment("If true, cancels direct vanilla melee/ranged weapon damage between players. Indirect damage remains allowed.")
            .define("directVanillaPvPDamageFilter", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int TC_MIN_LEVEL;
    public static int TC_MAX_LEVEL;
    public static int TC_BASE_FOOTPRINT;
    public static int TC_MAX_FOOTPRINT;
    public static int TC_VERTICAL_RADIUS;

    public static int UPKEEP_STRAW;
    public static int UPKEEP_WOOD;
    public static int UPKEEP_STONE;
    public static int UPKEEP_METAL;
    public static int UPKEEP_HQ;

    public static int DECAY_INTERVAL_HOURS;

    public static int C4_DAMAGE;
    public static int DRILL_DAMAGE_PER_SECOND;
    public static int DRILL_DURABILITY_COST;

    public static double WEAPON_DAMAGE;
    public static int WEAPON_COOLDOWN_TICKS;

    public static int PVP_SLEEPING_BAG_COOLDOWN_SECONDS;
    public static boolean DIRECT_VANILLA_PVP_DAMAGE_FILTER;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        TC_MIN_LEVEL = TC_MIN_LEVEL_CONFIG.get();
        TC_MAX_LEVEL = TC_MAX_LEVEL_CONFIG.get();
        TC_BASE_FOOTPRINT = TC_BASE_FOOTPRINT_CONFIG.get();
        TC_MAX_FOOTPRINT = TC_MAX_FOOTPRINT_CONFIG.get();
        TC_VERTICAL_RADIUS = TC_VERTICAL_RADIUS_CONFIG.get();

        UPKEEP_STRAW = UPKEEP_STRAW_CONFIG.get();
        UPKEEP_WOOD = UPKEEP_WOOD_CONFIG.get();
        UPKEEP_STONE = UPKEEP_STONE_CONFIG.get();
        UPKEEP_METAL = UPKEEP_METAL_CONFIG.get();
        UPKEEP_HQ = UPKEEP_HQ_CONFIG.get();

        DECAY_INTERVAL_HOURS = DECAY_INTERVAL_HOURS_CONFIG.get();

        C4_DAMAGE = C4_DAMAGE_CONFIG.get();
        DRILL_DAMAGE_PER_SECOND = DRILL_DAMAGE_PER_SECOND_CONFIG.get();
        DRILL_DURABILITY_COST = DRILL_DURABILITY_COST_CONFIG.get();

        WEAPON_DAMAGE = WEAPON_DAMAGE_CONFIG.get();
        WEAPON_COOLDOWN_TICKS = WEAPON_COOLDOWN_TICKS_CONFIG.get();

        PVP_SLEEPING_BAG_COOLDOWN_SECONDS = PVP_SLEEPING_BAG_COOLDOWN_SECONDS_CONFIG.get();
        DIRECT_VANILLA_PVP_DAMAGE_FILTER = DIRECT_VANILLA_PVP_DAMAGE_FILTER_CONFIG.get();
    }
}
