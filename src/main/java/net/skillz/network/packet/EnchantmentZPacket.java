package net.skillz.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class EnchantmentZPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.id("enchantmentz_packet");

    public Map<String, Integer> indexed() {
        return indexed;
    }

    public List<Integer> keys() {
        return keys;
    }

    public List<String> ids() {
        return ids;
    }

    public List<Integer> levels() {
        return levels;
    }

    protected final Map<String, Integer> indexed;
    protected final List<Integer> keys;
    protected final List<String> ids;
    protected final List<Integer> levels;

    public static final PacketType<EnchantmentZPacket> TYPE = PacketType.create(
            PACKET_ID, EnchantmentZPacket::new
    );

    public EnchantmentZPacket(PacketByteBuf buf) {
        this(buf.readMap(PacketByteBuf::readString, PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readString), buf.readList(PacketByteBuf::readInt));
    }

    public EnchantmentZPacket(Map<String, Integer> indexed, List<Integer> keys, List<String> ids, List<Integer> levels) {
        this.indexed = indexed;
        this.keys = keys;
        this.ids = ids;
        this.levels = levels;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeMap(this.indexed, PacketByteBuf::writeString, PacketByteBuf::writeInt);
        buf.writeCollection(this.keys, PacketByteBuf::writeInt);
        buf.writeCollection(this.ids, PacketByteBuf::writeString);
        buf.writeCollection(this.levels, PacketByteBuf::writeInt);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
