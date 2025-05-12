package net.skillz.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.skillz.SkillZMain;
import net.skillz.init.ConfigInit;
import net.skillz.level.LevelManager;
import net.skillz.level.Skill;
import net.skillz.level.SkillAttribute;
import net.skillz.level.SkillBonus;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.skillz.util.FileUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class SkillLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return SkillZMain.id("skills");
    }

    @Override
    public void reload(ResourceManager manager) {
        // clear skills
        LevelManager.SKILLS.clear();
        // clear bonuses
        LevelManager.BONUSES.clear();

        manager.findResources("skill", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                if (!ConfigInit.MAIN.PROGRESSION.defaultSkills && id.getNamespace().equals("skillz"))
                    return;

                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                String skillId = FileUtil.getBaseName(id.getPath());

                int maxLevel = data.get("maxlevel").getAsInt();
                List<SkillAttribute> attributes = new ArrayList<>();

                for (JsonElement attributeElement : data.getAsJsonArray("attributes")) {
                    JsonObject attributeJsonObject = attributeElement.getAsJsonObject();

                    //TODO EntityAttribute registry keys
                    Identifier iden = new Identifier(attributeJsonObject.get("type").getAsString());

                    RegistryKey<EntityAttribute> asd = RegistryKey.of(RegistryKeys.ATTRIBUTE, iden);
                    Optional<RegistryEntry.Reference<EntityAttribute>> entityAttribute = Registries.ATTRIBUTE.getEntry(asd);

                    if (entityAttribute.isPresent()) {
                        boolean hidden = attributeJsonObject.has("hidden") && attributeJsonObject.get("hidden").getAsBoolean();
                        RegistryEntry<EntityAttribute> attibute = entityAttribute.get();
                        float baseValue = -10000.0f;

                        if (attributeJsonObject.has("base")) {
                            baseValue = attributeJsonObject.get("base").getAsFloat();
                        }
                        float levelValue = attributeJsonObject.get("value").getAsFloat();
                        EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.valueOf(attributeJsonObject.get("operation").getAsString().toUpperCase());
                        attributes.add(new SkillAttribute(hidden, attibute, baseValue, levelValue, operation));
                    } else {
                        SkillZMain.LOGGER.warn("Attribute {} is not a usable attribute in skill {}.", attributeJsonObject.get("type").getAsString(), data.get("id").getAsString());
                    }
                }

                if (data.has("bonus")) {
                    for (JsonElement attributeElement : data.getAsJsonArray("bonus")) {
                        JsonObject bonusJsonObject = attributeElement.getAsJsonObject();
                        String bonusKey = bonusJsonObject.get("key").getAsString();
                        int bonusLevel = bonusJsonObject.get("level").getAsInt();

                        if (!SkillBonus.BONUS_KEYS.contains(bonusKey)) {
                            SkillZMain.LOGGER.warn("Bonus type {} is not a valid bonus type.", bonusKey);
                            continue;
                        }

                        LevelManager.BONUSES.put(bonusKey, new SkillBonus(bonusKey, skillId, bonusLevel));
                    }
                }
                LevelManager.SKILLS.put(skillId, new Skill(skillId, maxLevel, attributes));
            } catch (Exception e) {
                SkillZMain.LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }
}
