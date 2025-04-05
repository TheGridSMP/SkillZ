package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class LevelPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.identifierOf("level_packet");
    protected final int overallLevel;
    protected final int skillPoints;
    protected final int totalLevelExperience;
    protected final float levelProgress;

    public static final PacketType<LevelPacket> TYPE = PacketType.create(
            PACKET_ID, LevelPacket::new
    );

    public LevelPacket(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat());
    }

    public LevelPacket(int overallLevel, int skillPoints, int totalLevelExperience, float levelProgress) {
        this.overallLevel = overallLevel;
        this.skillPoints = skillPoints;
        this.totalLevelExperience = totalLevelExperience;
        this.levelProgress = levelProgress;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.overallLevel);
        buf.writeInt(this.skillPoints);
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

    public int skillPoints() {
        return skillPoints;
    }

    public int totalLevelExperience() {
        return totalLevelExperience;
    }

    public float levelProgress() {
        return levelProgress;
    }
}