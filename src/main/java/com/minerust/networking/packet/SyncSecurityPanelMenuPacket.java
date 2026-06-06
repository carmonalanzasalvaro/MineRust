package com.minerust.networking.packet;

import com.minerust.client.SecurityPanelMenuClientSync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncSecurityPanelMenuPacket {
    private final int tier;

    public SyncSecurityPanelMenuPacket(int tier) {
        this.tier = tier;
    }

    public static void encode(SyncSecurityPanelMenuPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.tier);
    }

    public static SyncSecurityPanelMenuPacket decode(FriendlyByteBuf buf) {
        return new SyncSecurityPanelMenuPacket(buf.readInt());
    }

    public static void handle(SyncSecurityPanelMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SecurityPanelMenuClientSync.applyTier(msg.tier)));
        ctx.get().setPacketHandled(true);
    }
}
