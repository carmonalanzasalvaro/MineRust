package com.minerust.registry;

import com.minerust.MineRustMod;
import com.minerust.claim.ToolCupboardBlock;
import com.minerust.respawn.SleepingBagBlock;
import com.minerust.raid.C4ChargeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MineRustMod.MODID);

    public static final RegistryObject<Block> TOOL_CUPBOARD_TIER1 = BLOCKS.register("tool_cupboard_tier1",
            () -> new ToolCupboardBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f), 1));

    public static final RegistryObject<Block> TOOL_CUPBOARD_TIER2 = BLOCKS.register("tool_cupboard_tier2",
            () -> new ToolCupboardBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f), 2));

    public static final RegistryObject<Block> SLEEPING_BAG = BLOCKS.register("sleeping_bag",
            () -> new SleepingBagBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));

    public static final RegistryObject<Block> C4_CHARGE = BLOCKS.register("c4_charge",
            () -> new C4ChargeBlock(BlockBehaviour.Properties.copy(Blocks.TNT)));

    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
