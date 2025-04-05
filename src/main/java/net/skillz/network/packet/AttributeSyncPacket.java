package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class AttributeSyncPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.identifierOf("attribute_sync_packet");

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
