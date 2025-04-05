package net.skillz.level;

import net.minecraft.text.Text;

import java.util.List;

public record Skill(int id, String key, int maxLevel, List<SkillAttribute> attributes) {

    public Text getText() {
        return Text.translatable("skill.skillz." + key);
    }

}
