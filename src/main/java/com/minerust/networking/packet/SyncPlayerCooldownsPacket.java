package com.minerust.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPlayerCooldownsPacket {

    // Skeleton: minimal fields for compile safety; real data model is not ready yet.
    private final UUID playerUuid;
    private final int cooldownSeconds;

    public SyncPlayerCooldownsPacket(UUID playerUuid, int cooldownSeconds) {
        this.playerUuid = playerUuid;
        this.cooldownSeconds = cooldownSeconds;
    }

    public static void encode(SyncPlayerCooldownsPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUuid);
        buf.writeInt(msg.cooldownSeconds);
    }

    public static SyncPlayerCooldownsPacket decode(FriendlyByteBuf buf) {
        UUID playerUuid = buf.readUUID();
        int cooldownSeconds = buf.readInt();
        return new SyncPlayerCooldownsPacket(playerUuid, cooldownSeconds);
    }

    public static void handle(SyncPlayerCooldownsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Intentionally empty until the data model is implemented in later tasks.
        });
        ctx.get().setPacketHandled(true);
    }
}
