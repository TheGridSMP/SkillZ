package net.skillz.level;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.skillz.util.Lazy;

public class PlayerPoints {

    private final Identifier id;
    private int level;

    private final Lazy<Skill> skill;

    public PlayerPoints(Identifier id, int level) {
        this.id = id;
        this.level = level;

        this.skill = new Lazy<>(() -> LevelManager.SKILLS.get(id));
    }

    public PlayerPoints(NbtCompound nbt) {
        this(new Identifier(nbt.getString("Id")), nbt.getInt("Level"));
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Id", this.id.toString());
        nbt.putInt("Level", this.level);
        return nbt;
    }

    public Identifier getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeIdentifier(id);
        buf.writeVarInt(level);
    }

    public static PlayerPoints fromBuf(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        int level = buf.readVarInt();

        return new PlayerPoints(id, level);
    }

    public Lazy<Skill> skill() {
        return skill;
    }
}
