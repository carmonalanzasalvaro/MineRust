package com.minerust.networking;

import com.minerust.MineRustMod;
import com.minerust.networking.packet.SyncBlockProtectionPacket;
import com.minerust.networking.packet.SyncPlayerCooldownsPacket;
import com.minerust.networking.packet.SyncSecurityPanelMenuPacket;
import com.minerust.networking.packet.SyncToolCupboardDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {

    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MineRustMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextId(),
                SyncToolCupboardDataPacket.class,
                SyncToolCupboardDataPacket::encode,
                SyncToolCupboardDataPacket::decode,
                SyncToolCupboardDataPacket::handle
        );

        CHANNEL.registerMessage(
                nextId(),
                SyncBlockProtectionPacket.class,
                SyncBlockProtectionPacket::encode,
                SyncBlockProtectionPacket::decode,
                SyncBlockProtectionPacket::handle
        );

        CHANNEL.registerMessage(
                nextId(),
                SyncPlayerCooldownsPacket.class,
                SyncPlayerCooldownsPacket::encode,
                SyncPlayerCooldownsPacket::decode,
                SyncPlayerCooldownsPacket::handle
        );

        CHANNEL.registerMessage(
                nextId(),
                SyncSecurityPanelMenuPacket.class,
                SyncSecurityPanelMenuPacket::encode,
                SyncSecurityPanelMenuPacket::decode,
                SyncSecurityPanelMenuPacket::handle
        );
    }
}
