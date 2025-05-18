package net.skillz.level;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SkillPoints(Identifier id, int start, int perLevel) {

    public void writeBuf(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeVarInt(start);
        buf.writeVarInt(perLevel);
    }

    public static SkillPoints fromBuf(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        int start = buf.readVarInt();
        int perLevel = buf.readVarInt();

        return new SkillPoints(id, start, perLevel);
    }
}
