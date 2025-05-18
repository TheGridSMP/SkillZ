package net.skillz.network.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.skillz.SkillZMain;
import net.skillz.level.restriction.PlayerRestriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.skillz.util.IntObjectBiConsumer;
/*import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;*/

import java.util.ArrayList;
import java.util.Map;

public class RestrictionPacket implements FabricPacket {
    public static final Identifier PACKET_ID = SkillZMain.id("restriction_packet");

    public RestrictionRecord blockRestrictions() {
        return blockRestrictions;
    }

    public RestrictionRecord craftingRestrictions() {
        return craftingRestrictions;
    }

    public RestrictionRecord entityRestrictions() {
        return entityRestrictions;
    }

    public RestrictionRecord itemRestrictions() {
        return itemRestrictions;
    }

    public RestrictionRecord miningRestrictions() {
        return miningRestrictions;
    }

    public RestrictionRecord enchantmentRestrictions() {
        return enchantmentRestrictions;
    }

    protected final RestrictionRecord blockRestrictions;
    protected final RestrictionRecord craftingRestrictions;
    protected final RestrictionRecord entityRestrictions;
    protected final RestrictionRecord itemRestrictions;
    protected final RestrictionRecord miningRestrictions;
    protected final RestrictionRecord enchantmentRestrictions;

    public static final PacketType<RestrictionPacket> TYPE = PacketType.create(
            PACKET_ID, RestrictionPacket::new
    );

    public RestrictionPacket(PacketByteBuf buf) {
        this(RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf), RestrictionRecord.read(buf));
    }

    public RestrictionPacket(RestrictionRecord blockRestrictions, RestrictionRecord craftingRestrictions, RestrictionRecord entityRestrictions,
                             RestrictionRecord itemRestrictions, RestrictionRecord miningRestrictions, RestrictionRecord enchantmentRestrictions) {
        this.blockRestrictions = blockRestrictions;
        this.craftingRestrictions = craftingRestrictions;
        this.entityRestrictions = entityRestrictions;
        this.itemRestrictions = itemRestrictions;
        this.miningRestrictions = miningRestrictions;
        this.enchantmentRestrictions = enchantmentRestrictions;
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.blockRestrictions.write(buf);
        this.craftingRestrictions.write(buf);
        this.entityRestrictions.write(buf);
        this.itemRestrictions.write(buf);
        this.miningRestrictions.write(buf);
        this.enchantmentRestrictions.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static class RestrictionRecord extends ArrayList<IntObjectPair<PlayerRestriction>> {

        public RestrictionRecord(int size) {
            super(size);
        }

        public static RestrictionRecord fromMap(Int2ObjectMap<PlayerRestriction> map) {
            RestrictionRecord record = new RestrictionRecord(map.size());

            map.forEach((integer, restriction) -> {
                record.add(IntObjectPair.of(integer, restriction));
            });

            return record;
        }

        public void write(PacketByteBuf buf) {
            buf.writeCollection(this, (b, pair) -> {
                b.writeVarInt(pair.leftInt());

                PlayerRestriction playerRestriction = pair.right();
                buf.writeInt(playerRestriction.id());
                buf.writeMap(playerRestriction.skillLevelRestrictions(), PacketByteBuf::writeIdentifier, PacketByteBuf::writeVarInt);
            });
        }

        public static RestrictionRecord read(PacketByteBuf buf) {
            return buf.readCollection(RestrictionRecord::new, b -> {
                int itemId = b.readInt();

                int id = buf.readInt();
                Map<Identifier, Integer> skillLevelRestrictions = b.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readVarInt);
                return IntObjectPair.of(itemId, new PlayerRestriction(id, skillLevelRestrictions));
            });
        }

        public void forEach(IntObjectBiConsumer<PlayerRestriction> f) {
            this.forEach(pair -> f.accept(pair.leftInt(), pair.right()));
        }
    }
}

