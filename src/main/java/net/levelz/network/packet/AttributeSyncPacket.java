package net.levelz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.levelz.LevelzMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class AttributeSyncPacket implements FabricPacket {
    public static final Identifier PACKET_ID = LevelzMain.identifierOf("attribute_sync_packet");

    public static final PacketType<AttributeSyncPacket> TYPE = PacketType.create(
            PACKET_ID, AttributeSyncPacket::new
    );

    public AttributeSyncPacket(PacketByteBuf buf) {
        this();
    }

    public AttributeSyncPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
