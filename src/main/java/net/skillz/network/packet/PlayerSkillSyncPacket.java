package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayerSkillSyncPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.identifierOf("player_skill_sync_packet");
    protected final List<Integer> playerSkillLevels;
    protected final List<Integer> playerSkillIds;

    public static final PacketType<PlayerSkillSyncPacket> TYPE = PacketType.create(
            PACKET_ID, PlayerSkillSyncPacket::new
    );

    public PlayerSkillSyncPacket(PacketByteBuf buf) {
        this(buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt));
    }

    public PlayerSkillSyncPacket(List<Integer> playerSkillIds, List<Integer> playerSkillLevels) {
        this.playerSkillIds = playerSkillIds;
        this.playerSkillLevels = playerSkillLevels;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.playerSkillIds, PacketByteBuf::writeInt);
        buf.writeCollection(this.playerSkillLevels, PacketByteBuf::writeInt);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
