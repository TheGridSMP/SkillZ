package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Increase skill packet
 * Used in the skill screen by using a button
 *
 * @param skillId    skill Id
 * @param level amount
 */
public record StatPacket(Identifier skillId, int level) implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.id("stat_packet");

    public static final PacketType<StatPacket> TYPE = PacketType.create(
            PACKET_ID, StatPacket::new
    );

    public StatPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readInt());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.skillId);
        buf.writeInt(this.level);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}