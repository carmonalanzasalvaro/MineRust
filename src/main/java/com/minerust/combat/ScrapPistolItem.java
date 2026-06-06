package com.minerust.combat;

import com.minerust.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ScrapPistolItem extends Item {
    private static final double RANGE = 20.0;

    public ScrapPistolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);

        ClipContext context = new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        BlockHitResult blockHit = level.clip(context);
        Vec3 rayEnd = blockHit.getLocation();
        double maxReach = eye.distanceTo(rayEnd);

        Entity target = findEntityOnRay(level, player, eye, look, maxReach);
        if (target == null) {
            player.displayClientMessage(Component.literal("No target in sight."), true);
            return InteractionResultHolder.fail(stack);
        }

        float damage = (float) Config.WEAPON_DAMAGE;
        target.hurt(level.damageSources().playerAttack(player), damage);
        player.getCooldowns().addCooldown(this, Config.WEAPON_COOLDOWN_TICKS);

        return InteractionResultHolder.success(stack);
    }

    private Entity findEntityOnRay(Level level, Player player, Vec3 eye, Vec3 look, double maxDistance) {
        Vec3 end = eye.add(look.x * maxDistance, look.y * maxDistance, look.z * maxDistance);
        AABB searchArea = player.getBoundingBox()
                .expandTowards(look.x * maxDistance, look.y * maxDistance, look.z * maxDistance)
                .inflate(1.0);
        Entity closest = null;
        double closestDist = maxDistance;
        for (Entity entity : level.getEntities(player, searchArea, e -> e.isAlive() && e != player)) {
            AABB bb = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> clip = bb.clip(eye, end);
            if (clip.isPresent()) {
                double dist = eye.distanceTo(clip.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }
        return closest;
    }
}
