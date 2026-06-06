package com.minerust.registry;

import com.minerust.MineRustMod;
import com.minerust.claim.ToolCupboardBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MineRustMod.MODID);

    public static final RegistryObject<BlockEntityType<ToolCupboardBlockEntity>> TOOL_CUPBOARD =
            BLOCK_ENTITIES.register("tool_cupboard",
                    () -> BlockEntityType.Builder.of(ToolCupboardBlockEntity::new,
                            ModBlocks.TOOL_CUPBOARD.get()).build(null));

    public static void init(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
