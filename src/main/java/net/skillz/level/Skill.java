package net.skillz.level;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.skillz.util.Lazy;
import net.skillz.util.PacketHelper;

import java.util.List;

public record Skill(Identifier id, Identifier pointsId, int maxLevel, List<SkillAttribute> attributes, Lazy<SkillPoints> points) {

    public Skill(Identifier id, Identifier pointsId, int maxLevel, List<SkillAttribute> attributes) {
        this(id, pointsId, maxLevel, attributes, new Lazy<>(() -> LevelManager.POINTS.get(pointsId)));
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeIdentifier(pointsId);
        buf.writeVarInt(maxLevel);
        PacketHelper.writeCollection(buf, attributes, SkillAttribute::writeBuf);
    }

    public static Skill fromBuf(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Identifier points = buf.readIdentifier();
        int maxLevel = buf.readVarInt();
        List<SkillAttribute> attributes = buf.readList(SkillAttribute::fromBuf);

        return new Skill(id, points, maxLevel, attributes);
    }

    public Text getText() {
        return Text.translatable(id.toTranslationKey("skill"));
    }
}
