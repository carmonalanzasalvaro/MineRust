package com.minerust.events;

import com.minerust.Config;
import com.minerust.MineRustMod;
import com.minerust.data.ClaimSavedData;
import com.minerust.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineRustMod.MODID)
public class RespawnEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ClaimSavedData data = ClaimSavedData.get(serverLevel);
        ClaimSavedData.PlayerCooldownData cooldownData = data.getPlayerCooldowns().get(player.getUUID());
        if (cooldownData == null) {
            cooldownData = new ClaimSavedData.PlayerCooldownData(player.getUUID());
        }

        if (event.getSource().getEntity() instanceof ServerPlayer) {
            cooldownData.setLastDeathWasPvp(true);
        } else {
            cooldownData.setLastDeathWasPvp(false);
        }

        data.setPlayerCooldown(player.getUUID(), cooldownData);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!(serverPlayer.level() instanceof ServerLevel currentLevel)) {
            return;
        }

        ClaimSavedData data = ClaimSavedData.get(currentLevel);
        ClaimSavedData.PlayerCooldownData cooldownData = data.getPlayerCooldowns().get(player.getUUID());
        if (cooldownData == null || !cooldownData.isLastDeathWasPvp()) {
            return;
        }

        if (cooldownData.isSleepingBagOnCooldown()) {
            cooldownData.setLastDeathWasPvp(false);
            data.setPlayerCooldown(player.getUUID(), cooldownData);
            return;
        }

        String dimStr = cooldownData.getSleepingBagDimension();
        long packedPos = cooldownData.getSleepingBagPos();
        cooldownData.setLastDeathWasPvp(false);

        if (dimStr == null || dimStr.isEmpty()) {
            data.setPlayerCooldown(player.getUUID(), cooldownData);
            return;
        }

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimStr));
        ServerLevel targetLevel = currentLevel.getServer().getLevel(dimensionKey);
        if (targetLevel == null) {
            data.setPlayerCooldown(player.getUUID(), cooldownData);
            return;
        }

        BlockPos pos = BlockPos.of(packedPos);
        if (!targetLevel.isLoaded(pos) || !targetLevel.getBlockState(pos).is(ModBlocks.SLEEPING_BAG.get())) {
            data.setPlayerCooldown(player.getUUID(), cooldownData);
            return;
        }

        serverPlayer.teleportTo(targetLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
        cooldownData.setSleepingBagCooldownEnd(System.currentTimeMillis() + Config.PVP_SLEEPING_BAG_COOLDOWN_SECONDS * 1000L);
        data.setPlayerCooldown(player.getUUID(), cooldownData);
    }
}
