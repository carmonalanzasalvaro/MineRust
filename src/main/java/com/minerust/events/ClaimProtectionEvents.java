package com.minerust.events;

import com.minerust.Config;
import com.minerust.MineRustMod;
import com.minerust.claim.ToolCupboardBlock;
import com.minerust.claim.ToolCupboardBlockEntity;
import com.minerust.claim.ToolCupboardClaimManager;
import com.minerust.data.ClaimSavedData;
import com.minerust.registry.ModBlocks;
import com.minerust.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

import java.util.Map;

@Mod.EventBusSubscriber(modid = MineRustMod.MODID)
public class ClaimProtectionEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();

        if (event.getState().getBlock() instanceof ToolCupboardBlock) {
            if (!ToolCupboardClaimManager.isAuthorized(level, pos, player.getUUID())) {
                player.displayClientMessage(Component.literal("You are not authorized to interact with this Security Panel."), true);
                event.setCanceled(true);
                return;
            }
            int tier = level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be ? be.getTier() : 1;
            ClaimSavedData.ToolCupboardData claim = ToolCupboardClaimManager.getClaimAt(level, pos);
            if (claim != null && claim.getTcPackedPos() == pos.asLong()) {
                tier = claim.getTier();
            }
            ToolCupboardClaimManager.removeClaim(level, pos, tier);
            return;
        }

        if (!ToolCupboardClaimManager.isAuthorized(level, pos, player.getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();

        if (event.getPlacedBlock().is(Blocks.FIRE) || event.getPlacedBlock().is(Blocks.SOUL_FIRE)) {
            ClaimSavedData data = ClaimSavedData.get(level);
            String dimension = level.dimension().location().toString();
            Map<Long, ClaimSavedData.ProtectedBlockData> protectedBlocks = data.getProtectedBlocksByDimension().get(dimension);
            if (protectedBlocks != null && protectedBlocks.containsKey(pos.asLong())) {
                event.setCanceled(true);
                return;
            }
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getPlacedBlock().getBlock() instanceof ToolCupboardBlock) {
            boolean registered = level.getBlockEntity(pos) instanceof ToolCupboardBlockEntity be && be.getOwner() != null;
            if (!registered || ToolCupboardClaimManager.wouldOverlapMaxCoverage(level, pos, pos)) {
                event.setCanceled(true);
                player.displayClientMessage(
                    Component.literal("Security Panels need enough space for max coverage: keep at least 30 blocks between panels."),
                    true
                );
                return;
            }
            return;
        }

        if (event.getPlacedBlock().getBlock() == ModBlocks.C4_CHARGE.get()) {
            return;
        }

        if (!ToolCupboardClaimManager.isAuthorized(level, pos, player.getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Player player = event.getEntity();
        BlockPos pos = event.getPos();

        if (level.getBlockState(pos).getBlock() == ModBlocks.C4_CHARGE.get()) {
            return;
        }

        if (player.getItemInHand(event.getHand()).is(ModItems.RAID_DRILL.get())) {
            return;
        }

        if (!ToolCupboardClaimManager.isAuthorized(level, pos, player.getUUID())) {
            if (level.getBlockState(pos).getBlock() instanceof ToolCupboardBlock) {
                player.displayClientMessage(Component.literal("You are not authorized to interact with this Security Panel."), true);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!Config.DIRECT_VANILLA_PVP_DAMAGE_FILTER) {
            return;
        }
        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!Config.DIRECT_VANILLA_PVP_DAMAGE_FILTER) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof Player)) {
            return;
        }
        Entity directEntity = event.getSource().getDirectEntity();
        if (directEntity instanceof AbstractArrow) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        Map<Long, ClaimSavedData.ProtectedBlockData> protectedBlocks = data.getProtectedBlocksByDimension().get(dimension);
        if (protectedBlocks == null || protectedBlocks.isEmpty()) {
            return;
        }
        event.getAffectedBlocks().removeIf(pos -> protectedBlocks.containsKey(pos.asLong()));
    }

    @SubscribeEvent
    public static void onPistonPre(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        PistonStructureResolver helper = event.getStructureHelper();
        if (helper == null) {
            return;
        }
        if (!helper.resolve()) {
            return;
        }
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        Map<Long, ClaimSavedData.ProtectedBlockData> protectedBlocks = data.getProtectedBlocksByDimension().get(dimension);
        if (protectedBlocks == null || protectedBlocks.isEmpty()) {
            return;
        }
        for (BlockPos pos : helper.getToPush()) {
            if (protectedBlocks.containsKey(pos.asLong())) {
                event.setCanceled(true);
                return;
            }
        }
        for (BlockPos pos : helper.getToDestroy()) {
            if (protectedBlocks.containsKey(pos.asLong())) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();
        ClaimSavedData data = ClaimSavedData.get(level);
        String dimension = level.dimension().location().toString();
        Map<Long, ClaimSavedData.ProtectedBlockData> protectedBlocks = data.getProtectedBlocksByDimension().get(dimension);
        if (protectedBlocks == null || protectedBlocks.isEmpty()) {
            return;
        }
        if (protectedBlocks.containsKey(pos.asLong())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (event.getServer().getTickCount() % 20 != 0) {
            return;
        }

        ClaimSavedData data = ClaimSavedData.get(event.getServer());
        Map<String, Map<Long, ClaimSavedData.ProtectedBlockData>> blocksByDim = data.getProtectedBlocksByDimension();
        if (blocksByDim.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Map<Long, ClaimSavedData.ProtectedBlockData>> dimEntry : blocksByDim.entrySet()) {
            String dimension = dimEntry.getKey();
            ResourceLocation rl = ResourceLocation.tryParse(dimension);
            if (rl == null) {
                continue;
            }
            ServerLevel level = event.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, rl));
            if (level == null) {
                continue;
            }

            Map<Long, ClaimSavedData.ProtectedBlockData> dimBlocks = dimEntry.getValue();
            if (dimBlocks.isEmpty()) {
                continue;
            }

            for (long packedPos : dimBlocks.keySet()) {
                BlockPos pos = BlockPos.of(packedPos);
                for (Direction dir : Direction.values()) {
                    BlockPos checkPos = pos.relative(dir);
                    if (level.getBlockState(checkPos).is(Blocks.FIRE) || level.getBlockState(checkPos).is(Blocks.SOUL_FIRE)) {
                        level.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
                if (level.getBlockState(pos).is(Blocks.FIRE) || level.getBlockState(pos).is(Blocks.SOUL_FIRE)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        if (event.player.tickCount % 10 != 0) {
            return;
        }
        if (!(event.player.level() instanceof ServerLevel level)) {
            return;
        }

        boolean holdingStaff = event.player.getMainHandItem().is(ModItems.PROTECTION_STAFF.get())
            || event.player.getOffhandItem().is(ModItems.PROTECTION_STAFF.get());
        boolean holdingDrill = event.player.getMainHandItem().is(ModItems.RAID_DRILL.get())
            || event.player.getOffhandItem().is(ModItems.RAID_DRILL.get());

        if (!holdingStaff && !holdingDrill) {
            return;
        }

        ClaimSavedData data = ClaimSavedData.get(level);
        Map<Long, ClaimSavedData.ProtectedBlockData> protectedBlocks = data.getProtectedBlocksByDimension().get(level.dimension().location().toString());
        if (protectedBlocks == null || protectedBlocks.isEmpty()) {
            return;
        }

        for (ClaimSavedData.ProtectedBlockData blockData : protectedBlocks.values()) {
            BlockPos blockPos = BlockPos.of(blockData.getPackedPos());
            double dx = blockPos.getX() + 0.5D - event.player.getX();
            double dy = blockPos.getY() + 0.5D - event.player.getY();
            double dz = blockPos.getZ() + 0.5D - event.player.getZ();
            if (dx * dx + dy * dy + dz * dz > 256.0D) {
                continue;
            }

            boolean placedByPlayer = event.player.getUUID().equals(blockData.getPlacedBy());
            if (holdingStaff && placedByPlayer) {
                showProtectedBlockMarker(level, blockPos, new DustParticleOptions(new Vector3f(0.20F, 1.00F, 0.20F), 2.0F));
            }
            if (holdingDrill && !placedByPlayer) {
                showProtectedBlockMarker(level, blockPos, new DustParticleOptions(new Vector3f(1.00F, 0.20F, 0.20F), 2.0F));
            }
        }
    }

    private static void showProtectedBlockMarker(ServerLevel level, BlockPos pos, DustParticleOptions particle) {
        double centerX = pos.getX() + 0.5D;
        double centerY = pos.getY() + 0.5D;
        double centerZ = pos.getZ() + 0.5D;

        for (int i = 0; i < 3; i++) {
            double offset = i * 0.25D - 0.25D;
            level.sendParticles(particle, centerX + offset, centerY, centerZ, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            level.sendParticles(particle, centerX, centerY + offset, centerZ, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            level.sendParticles(particle, centerX, centerY, centerZ + offset, 1, 0.05D, 0.05D, 0.05D, 0.0D);
        }
        level.sendParticles(particle, centerX + 0.4D, centerY + 0.4D, centerZ + 0.4D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(particle, centerX - 0.4D, centerY + 0.4D, centerZ - 0.4D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(particle, centerX + 0.4D, centerY + 0.4D, centerZ - 0.4D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(particle, centerX - 0.4D, centerY + 0.4D, centerZ + 0.4D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }
}
