package net.skillz.level.restriction;

import java.util.Map;

public class PlayerRestriction {

    private final int id;
    private final Map<String, Integer> skillLevelRestrictions; // skillid, lvl

    public PlayerRestriction(int id, Map<String, Integer> skillLevelRestrictions) {
        this.id = id;
        this.skillLevelRestrictions = skillLevelRestrictions;
    }

    public int getId() {
        return id;
    }

    public Map<String, Integer> getSkillLevelRestrictions() {
        return skillLevelRestrictions;
    }
}
