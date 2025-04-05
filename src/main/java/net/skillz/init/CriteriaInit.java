package net.skillz.init;

import net.skillz.criteria.LevelZCriterion;
import net.skillz.criteria.SkillCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class CriteriaInit {

    public static final LevelZCriterion LEVEL_UP = Criteria.register(new LevelZCriterion());
    public static final SkillCriterion SKILL_UP = Criteria.register(new SkillCriterion());
    public static final ScoreboardCriterion LEVELZ = ScoreboardCriterion.create("levelz");

    public static void init() {
    }

}
