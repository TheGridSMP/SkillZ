package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.util.Identifier;
import net.skillz.SkillZMain;
import net.skillz.entity.LevelExperienceOrbEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class OrbPacket {

    public static final Identifier PACKET_ID = SkillZMain.identifierOf("orb");

    private final int entityId;
    private final double x;
    private final double y;
    private final double z;
    private final int experience;

    public OrbPacket(PacketByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.experience = buf.readShort();
    }

    public OrbPacket(LevelExperienceOrbEntity orb) {
        this.entityId = orb.getId();
        this.x = orb.getX();
        this.y = orb.getY();
        this.z = orb.getZ();
        this.experience = orb.getExperienceAmount();
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeShort(this.experience);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getExperience() {
        return this.experience;
    }

    public static Packet<ClientPlayPacketListener> createS2C(LevelExperienceOrbEntity orb) {
        PacketByteBuf buf = PacketByteBufs.create();
        new OrbPacket(orb).write(buf);

        return ServerPlayNetworking.createS2CPacket(PACKET_ID, buf);
    }
}