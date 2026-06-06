package com.minerust.item;

import com.minerust.claim.ToolCupboardClaimManager;
import com.minerust.data.ClaimSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Set;

public class ClaimDebugStickItem extends Item {
    private static final int SCAN_RADIUS_CHUNKS = 5;
    private static final DustParticleOptions CORNER_BEAM_PARTICLE = new DustParticleOptions(new Vector3f(0.15F, 0.85F, 1.00F), 2.8F);

    public ClaimDebugStickItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        String dimension = serverLevel.dimension().location().toString();
        BlockPos playerPos = player.blockPosition();

        ClaimSavedData.ToolCupboardData claim = ToolCupboardClaimManager.getClaimAt(serverLevel, playerPos);

        if (claim == null) {
            player.displayClientMessage(Component.literal("No Security Panel claim covers this block."), false);
        } else {
            BlockPos center = BlockPos.of(claim.getTcPackedPos());
            ToolCupboardClaimManager.ClaimBounds bounds = ToolCupboardClaimManager.getBounds(claim);

            player.displayClientMessage(Component.literal("Security Panel claim found:"), false);
            player.displayClientMessage(Component.literal("Owner UUID: " + claim.getOwner()), false);
            player.displayClientMessage(Component.literal("Level: " + ToolCupboardClaimManager.clampLevel(claim.getTier()) + "/" + ToolCupboardClaimManager.getMaxLevel()), false);
            player.displayClientMessage(Component.literal("Coverage: " + bounds.sizeX() + "x" + bounds.sizeY() + "x" + bounds.sizeZ() + " blocks"), false);
            player.displayClientMessage(Component.literal("Panel center: " + center.getX() + ", " + center.getY() + ", " + center.getZ()), false);
            player.displayClientMessage(Component.literal("Current block: " + playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ()), false);
            player.displayClientMessage(Component.literal("Covered X/Z: " + bounds.minX() + ", " + bounds.minZ() + " -> " + bounds.maxX() + ", " + bounds.maxZ()), false);
            player.displayClientMessage(Component.literal("Covered Y: " + bounds.minY() + " -> " + bounds.maxY()), false);
        }

        outlineNearbyClaims(serverLevel, player, dimension);
        return InteractionResultHolder.success(stack);
    }

    private static void outlineNearbyClaims(ServerLevel level, Player player, String dimension) {
        ClaimSavedData data = ClaimSavedData.get(level);
        Set<Long> outlinedClaims = new HashSet<>();

        for (ClaimSavedData.ToolCupboardData claim : data.getClaims(dimension)) {
            BlockPos center = BlockPos.of(claim.getTcPackedPos());
            if (Math.abs(center.getX() - player.getBlockX()) > SCAN_RADIUS_CHUNKS * 16
                    || Math.abs(center.getZ() - player.getBlockZ()) > SCAN_RADIUS_CHUNKS * 16) {
                continue;
            }
            if (!outlinedClaims.add(claim.getTcPackedPos())) {
                continue;
            }

            outlineClaimVolume(level, claim);
        }
    }

    private static void outlineClaimVolume(ServerLevel level, ClaimSavedData.ToolCupboardData claim) {
        ToolCupboardClaimManager.ClaimBounds bounds = ToolCupboardClaimManager.getBounds(claim);
        double minX = bounds.minX();
        double minY = bounds.minY();
        double minZ = bounds.minZ();
        double maxX = bounds.maxX() + 1.0D;
        double maxY = bounds.maxY() + 1.0D;
        double maxZ = bounds.maxZ() + 1.0D;

        for (double x = minX; x <= maxX; x += 2.0D) {
            particle(level, x, minY, minZ);
            particle(level, x, minY, maxZ);
            particle(level, x, maxY, minZ);
            particle(level, x, maxY, maxZ);
        }
        for (double z = minZ; z <= maxZ; z += 2.0D) {
            particle(level, minX, minY, z);
            particle(level, maxX, minY, z);
            particle(level, minX, maxY, z);
            particle(level, maxX, maxY, z);
        }
        for (double y = minY; y <= maxY; y += 2.0D) {
            particle(level, minX, y, minZ);
            particle(level, maxX, y, minZ);
            particle(level, minX, y, maxZ);
            particle(level, maxX, y, maxZ);
        }

        cornerBeam(level, minX, minY, minZ, maxY);
        cornerBeam(level, maxX, minY, minZ, maxY);
        cornerBeam(level, minX, minY, maxZ, maxY);
        cornerBeam(level, maxX, minY, maxZ, maxY);
    }

    private static void particle(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    private static void cornerBeam(ServerLevel level, double x, double minY, double z, double maxY) {
        for (double y = minY; y <= maxY; y += 1.0D) {
            beamParticle(level, x, y, z);
            beamParticle(level, x + 0.18D, y, z);
            beamParticle(level, x - 0.18D, y, z);
            beamParticle(level, x, y, z + 0.18D);
            beamParticle(level, x, y, z - 0.18D);
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.0D, 0.04D, 0.0D, 0.0D);
        }
    }

    private static void beamParticle(ServerLevel level, double x, double y, double z) {
        level.sendParticles(CORNER_BEAM_PARTICLE, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

}
