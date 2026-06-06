package com.minerust.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBlockProtectionPacket {

    // Skeleton: minimal field for compile safety; real data model is not ready yet.
    private final long packedPos;

    public SyncBlockProtectionPacket(long packedPos) {
        this.packedPos = packedPos;
    }

    public static void encode(SyncBlockProtectionPacket msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.packedPos);
    }

    public static SyncBlockProtectionPacket decode(FriendlyByteBuf buf) {
        long packedPos = buf.readLong();
        return new SyncBlockProtectionPacket(packedPos);
    }

    public static void handle(SyncBlockProtectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Intentionally empty until the data model is implemented in later tasks.
        });
        ctx.get().setPacketHandled(true);
    }
}
