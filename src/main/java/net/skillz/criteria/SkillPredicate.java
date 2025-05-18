package net.skillz.criteria;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SkillPredicate {
    private final Identifier jobName;

    public SkillPredicate(Identifier jobName) {
        this.jobName = jobName;
    }

    public boolean test(Identifier jobName) {
        return this.jobName.equals(jobName);
    }

    public static SkillPredicate fromJson(JsonElement json) {
        String jobName = JsonHelper.asString(json, "skill_name");
        return new SkillPredicate(new Identifier(jobName));
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("skill_name", this.jobName.toString());
        return jsonObject;
    }
}
