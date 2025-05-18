package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.skillz.level.Skill;
import net.skillz.level.SkillBonus;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.skillz.level.SkillPoints;
import net.skillz.util.PacketHelper;

import java.util.Collection;
import java.util.List;

public record SkillSyncPacket(Collection<Skill> skills, Collection<SkillBonus> skillBonuses, Collection<SkillPoints> skillPoints) implements FabricPacket {

    public static final Identifier PACKET_ID = SkillZMain.id("skill_sync_packet");

    public static final PacketType<SkillSyncPacket> TYPE = PacketType.create(
            PACKET_ID, SkillSyncPacket::fromBuf
    );

    public static SkillSyncPacket fromBuf(PacketByteBuf buf) {
        List<Skill> skills = buf.readList(Skill::fromBuf);
        List<SkillBonus> bonuses = buf.readList(SkillBonus::fromBuf);
        List<SkillPoints> points = buf.readList(SkillPoints::fromBuf);

        return new SkillSyncPacket(skills, bonuses, points);
    }

    @Override
    public void write(PacketByteBuf buf) {
        PacketHelper.writeCollection(buf, this.skills, Skill::writeBuf);
        PacketHelper.writeCollection(buf, this.skillBonuses, SkillBonus::writeBuf);
        PacketHelper.writeCollection(buf, this.skillPoints, SkillPoints::writeBuf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}