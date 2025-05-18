package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.skillz.level.PlayerPoints;
import net.skillz.util.PacketHelper;

import java.util.Map;

public class LevelPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.id("level_packet");
    protected final int overallLevel;
    protected final Map<Identifier, PlayerPoints> skillPoints;
    protected final int totalLevelExperience;
    protected final float levelProgress;

    public static final PacketType<LevelPacket> TYPE = PacketType.create(
            PACKET_ID, LevelPacket::new
    );

    public LevelPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readMap(PacketByteBuf::readIdentifier, PlayerPoints::fromBuf), buf.readInt(), buf.readFloat());
    }

    public LevelPacket(int overallLevel, Map<Identifier, PlayerPoints> points, int totalLevelExperience, float levelProgress) {
        this.overallLevel = overallLevel;
        this.skillPoints = points;
        this.totalLevelExperience = totalLevelExperience;
        this.levelProgress = levelProgress;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.overallLevel);
        PacketHelper.writeMap(buf, this.skillPoints, PacketByteBuf::writeIdentifier, PlayerPoints::writeBuf);
        buf.writeInt(this.totalLevelExperience);
        buf.writeFloat(this.levelProgress);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int overallLevel() {
        return overallLevel;
    }

    public Map<Identifier, PlayerPoints> skillPoints() {
        return skillPoints;
    }

    public int totalLevelExperience() {
        return totalLevelExperience;
    }

    public float levelProgress() {
        return levelProgress;
    }
}