package net.skillz.init;

import net.skillz.criteria.LevelCriterion;
import net.skillz.criteria.SkillCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final LevelCriterion LEVEL_UP = Criteria.register(new LevelCriterion());
    public static final SkillCriterion SKILL_UP = Criteria.register(new SkillCriterion());
    public static final ScoreboardCriterion SKILLZ = ScoreboardCriterion.create("skillz");

    public static void init() {
    }

}
