package net.skillz.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.skillz.SkillZMain;
import net.skillz.init.ConfigInit;
import net.skillz.level.LevelManager;
import net.skillz.level.restriction.PlayerRestriction;
import net.skillz.registry.EnchantmentRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.skillz.util.FileUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.skillz.SkillZMain.LOGGER;

public class RestrictionLoader implements SimpleSynchronousResourceReloadListener {

    public static final Identifier ID = SkillZMain.id("restriction");

    private static final List<Integer> blockList = new ArrayList<>();
    private static final List<Integer> craftingList = new ArrayList<>();
    private static final List<Integer> entityList = new ArrayList<>();
    private static final List<Integer> itemList = new ArrayList<>();
    private static final List<Integer> miningList = new ArrayList<>();
    private static final List<Integer> enchantmentList = new ArrayList<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        LevelManager.BLOCK_RESTRICTIONS.clear();
        LevelManager.CRAFTING_RESTRICTIONS.clear();
        LevelManager.ENTITY_RESTRICTIONS.clear();
        LevelManager.ITEM_RESTRICTIONS.clear();
        LevelManager.MINING_RESTRICTIONS.clear();
        LevelManager.ENCHANTMENT_RESTRICTIONS.clear();

        if (!ConfigInit.MAIN.PROGRESSION.restrictions) return;
        EnchantmentRegistry.updateEnchantments();

        manager.findResources("restriction", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                if (!ConfigInit.MAIN.PROGRESSION.defaultRestrictions && id.getNamespace().equals("skillz")) {
                    return;
                }
                InputStream stream = resourceRef.getInputStream();
                JsonArray data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();

                String restrictionFile = FileUtil.getBaseName(id.getPath());

                for (JsonElement element : data) {
                    JsonObject restrictionJsonObject = element.getAsJsonObject();
                    Map<Identifier, Integer> skillLevelRestrictions = new HashMap<>();
                    boolean replace = restrictionJsonObject.has("replace") && restrictionJsonObject.get("replace").getAsBoolean();

                    JsonObject skillRestrictions = restrictionJsonObject.getAsJsonObject("skills");
                    for (String skillKey : skillRestrictions.keySet()) {
                        Identifier skillId = new Identifier(skillKey);
                        if (LevelManager.SKILLS.containsKey(skillId)) {
                            skillLevelRestrictions.put(skillId, skillRestrictions.get(skillKey).getAsInt());
                        } else {
                            LOGGER.warn("Restriction {} contains an unrecognized skill called {}.", restrictionFile, skillKey);
                        }
                    }

                    if (!skillLevelRestrictions.isEmpty()) {
                        // blocks
                        if (restrictionJsonObject.has("blocks")) {
                            for (JsonElement blockElement : restrictionJsonObject.getAsJsonArray("blocks")) {
                                Identifier blockIdentifier = new Identifier(blockElement.getAsString());
                                if (Registries.BLOCK.containsId(blockIdentifier)) {
                                    int blockRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(blockIdentifier));

                                    if (blockList.contains(blockRawId))
                                        continue;

                                    if (replace)
                                        blockList.add(blockRawId);

                                    LevelManager.BLOCK_RESTRICTIONS.put(blockRawId, new PlayerRestriction(blockRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized block skillId called {}.", restrictionFile, blockIdentifier);
                                }
                            }
                        }
                        // crafting
                        if (restrictionJsonObject.has("crafting")) {
                            for (JsonElement craftingElement : restrictionJsonObject.getAsJsonArray("crafting")) {
                                Identifier craftingIdentifier = new Identifier(craftingElement.getAsString());
                                if (Registries.ITEM.containsId(craftingIdentifier)) {
                                    int craftingRawId = Registries.ITEM.getRawId(Registries.ITEM.get(craftingIdentifier));

                                    if (craftingList.contains(craftingRawId))
                                        continue;

                                    if (replace)
                                        craftingList.add(craftingRawId);

                                    LevelManager.CRAFTING_RESTRICTIONS.put(craftingRawId, new PlayerRestriction(craftingRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized crafting skillId called {}.", restrictionFile, craftingIdentifier);
                                }
                            }
                        }
                        // entities
                        if (restrictionJsonObject.has("entities")) {
                            for (JsonElement entityElement : restrictionJsonObject.getAsJsonArray("entities")) {
                                Identifier entityIdentifier = new Identifier(entityElement.getAsString());
                                if (Registries.ENTITY_TYPE.containsId(entityIdentifier)) {
                                    int entityRawId = Registries.ENTITY_TYPE.getRawId(Registries.ENTITY_TYPE.get(entityIdentifier));

                                    if (entityList.contains(entityRawId))
                                        continue;

                                    if (replace)
                                        entityList.add(entityRawId);

                                    LevelManager.ENTITY_RESTRICTIONS.put(entityRawId, new PlayerRestriction(entityRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized entity skillId called {}.", restrictionFile, entityIdentifier);
                                }
                            }
                        }
                        // items
                        if (restrictionJsonObject.has("items")) {
                            for (JsonElement itemElement : restrictionJsonObject.getAsJsonArray("items")) {
                                Identifier itemIdentifier = new Identifier(itemElement.getAsString());
                                if (Registries.ITEM.containsId(itemIdentifier)) {
                                    int itemRawId = Registries.ITEM.getRawId(Registries.ITEM.get(itemIdentifier));

                                    if (itemList.contains(itemRawId))
                                        continue;

                                    if (replace)
                                        itemList.add(itemRawId);

                                    LevelManager.ITEM_RESTRICTIONS.put(itemRawId, new PlayerRestriction(itemRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized item skillId called {}.", restrictionFile, itemIdentifier);
                                }
                            }
                        }
                        // mining
                        if (restrictionJsonObject.has("mining")) {
                            for (JsonElement miningElement : restrictionJsonObject.getAsJsonArray("mining")) {
                                Identifier miningIdentifier = new Identifier(miningElement.getAsString());
                                if (Registries.BLOCK.containsId(miningIdentifier)) {
                                    int miningRawId = Registries.BLOCK.getRawId(Registries.BLOCK.get(miningIdentifier));

                                    if (miningList.contains(miningRawId))
                                        continue;

                                    if (replace)
                                        miningList.add(miningRawId);

                                    LevelManager.MINING_RESTRICTIONS.put(miningRawId, new PlayerRestriction(miningRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized mining skillId called {}.", restrictionFile, miningIdentifier);
                                }
                            }
                        }
                        // enchantments
                        if (restrictionJsonObject.has("enchantments")) {
                            JsonObject enchantmentObject = restrictionJsonObject.getAsJsonObject("enchantments");
                            for (String enchantment : enchantmentObject.keySet()) {
                                Identifier enchantmentIdentifier = new Identifier(enchantment);
                                int level = enchantmentObject.get(enchantment).getAsInt();
                                if (EnchantmentRegistry.containsId(enchantmentIdentifier, level)) {
                                    int enchantmentRawId = EnchantmentRegistry.getId(enchantmentIdentifier, level);

                                    if (enchantmentList.contains(enchantmentRawId))
                                        continue;

                                    if (replace)
                                        enchantmentList.add(enchantmentRawId);

                                    LevelManager.ENCHANTMENT_RESTRICTIONS.put(enchantmentRawId, new PlayerRestriction(enchantmentRawId, skillLevelRestrictions));
                                } else {
                                    LOGGER.warn("Restriction {} contains an unrecognized enchantment skillId called {}.", restrictionFile, enchantmentIdentifier);
                                }
                            }
                        }
                    } else {
                        LOGGER.warn("Restriction {} does not contain any valid skills.", restrictionFile);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }
}
