package net.skillz.level;

import net.minecraft.nbt.NbtCompound;

public class PlayerSkill {

    private final String id;
    private int level;

    public PlayerSkill(String id, int level) {
        this.id = id;
        this.level = level;
    }

    public PlayerSkill(NbtCompound nbt) {
        this.id = nbt.getString("Id");
        this.level = nbt.getInt("Level");
    }

    public NbtCompound writeDataToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Id", this.id);
        nbt.putInt("Level", this.level);
        return nbt;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void increaseLevel(int level) {
        int maxLevel = LevelManager.SKILLS.get(this.id).maxLevel();
        if ((this.level + level) <= maxLevel) {
            this.level += level;
        } else {
            this.level = maxLevel;
        }
    }

    public void decreaseLevel(int level) {
        if ((this.level - level) >= 0) {
            this.level -= level;
        } else {
            this.level = 0;
        }
    }

}
