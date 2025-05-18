package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.skillz.level.PlayerSkill;
import net.skillz.util.PacketHelper;

import java.util.Collection;

public record PlayerSkillSyncPacket(Collection<PlayerSkill> playerSkills) implements FabricPacket {

    public static final Identifier PACKET_ID = SkillZMain.id("player_skill_sync_packet");

    public static final PacketType<PlayerSkillSyncPacket> TYPE = PacketType.create(
            PACKET_ID, PlayerSkillSyncPacket::new
    );

    public PlayerSkillSyncPacket(PacketByteBuf buf) {
        this(buf.readList(PlayerSkill::fromBuf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        PacketHelper.writeCollection(buf, playerSkills, PlayerSkill::writeBuf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
