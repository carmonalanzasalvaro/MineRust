package com.minerust.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncToolCupboardDataPacket {

    // Skeleton: minimal field for compile safety; real data model is not ready yet.
    private final int placeholderId;

    public SyncToolCupboardDataPacket(int placeholderId) {
        this.placeholderId = placeholderId;
    }

    public static void encode(SyncToolCupboardDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.placeholderId);
    }

    public static SyncToolCupboardDataPacket decode(FriendlyByteBuf buf) {
        int placeholderId = buf.readInt();
        return new SyncToolCupboardDataPacket(placeholderId);
    }

    public static void handle(SyncToolCupboardDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Intentionally empty until the data model is implemented in later tasks.
        });
        ctx.get().setPacketHandled(true);
    }
}
