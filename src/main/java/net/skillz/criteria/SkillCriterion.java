package net.skillz.criteria;

import com.google.gson.JsonObject;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SkillCriterion extends AbstractCriterion<SkillCriterion.Conditions> {
    static final Identifier ID = new Identifier("skillz:skill");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        SkillPredicate skillPredicate = SkillPredicate.fromJson(jsonObject.get("skill_name"));
        NumberPredicate skillLevelPredicate = NumberPredicate.fromJson(jsonObject.get("skill_level"));
        return new Conditions(lootContextPredicate, skillPredicate, skillLevelPredicate);
    }

    public void trigger(ServerPlayerEntity player, Identifier skillName, int skillLevel) {
        this.trigger(player, conditions -> conditions.matches(skillName, skillLevel));
    }

    public static class Conditions extends AbstractCriterionConditions {
        private final SkillPredicate skillPredicate;
        private final NumberPredicate skillLevelPredicate;

        public Conditions(LootContextPredicate lootContextPredicate, SkillPredicate skillPredicate, NumberPredicate skillLevelPredicate) {
            super(ID, lootContextPredicate);
            this.skillPredicate = skillPredicate;
            this.skillLevelPredicate = skillLevelPredicate;
        }

        public boolean matches(Identifier skillName, int skillLevel) {
            return this.skillPredicate.test(skillName) && skillLevelPredicate.test(skillLevel);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("skill_name", this.skillPredicate.toJson());
            jsonObject.add("skill_level", this.skillLevelPredicate.toJson());
            return jsonObject;
        }
    }
}
