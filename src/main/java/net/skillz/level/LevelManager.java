package net.skillz.level;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import net.skillz.init.ConfigInit;
import net.skillz.level.restriction.PlayerRestriction;
import net.skillz.registry.EnchantmentRegistry;
import net.skillz.util.LevelHelper;
import net.skillz.util.PacketHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class LevelManager {

    public static final Map<Identifier, Skill> SKILLS = new HashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> BLOCK_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> CRAFTING_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> ENTITY_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> ITEM_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> MINING_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Int2ObjectMap<PlayerRestriction> ENCHANTMENT_RESTRICTIONS = new Int2ObjectOpenHashMap<>();
    public static final Map<String, SkillBonus> BONUSES = new HashMap<>();
    public static final Map<Identifier, SkillPoints> POINTS = new HashMap<>();

    private final PlayerEntity playerEntity;
    private Map<Identifier, PlayerSkill> playerSkills = new HashMap<>();

    // Level
    private int overallLevel;
    private int totalLevelExperience;
    private float levelProgress;
    private Map<Identifier, PlayerPoints> skillPoints = new HashMap<>();

    public LevelManager(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;

        for (Skill skill : SKILLS.values()) {
            if (!this.playerSkills.containsKey(skill.id())) {
                this.playerSkills.put(skill.id(), new PlayerSkill(skill.id(), 0));
            } else if (this.playerSkills.get(skill.id()).getLevel() > skill.maxLevel()) {
                this.playerSkills.get(skill.id()).setLevel(skill.maxLevel());
            }
        }
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void readNbt(NbtCompound nbt) {
        this.overallLevel = nbt.getInt("Level");
        this.levelProgress = nbt.getFloat("LevelProgress");
        this.totalLevelExperience = nbt.getInt("TotalLevelExperience");

        NbtList points = nbt.getList("SkillPoints", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < points.size(); i++) {
            PlayerPoints point = new PlayerPoints(points.getCompound(i));
            skillPoints.put(point.getId(), point);
        }

        NbtList skills = nbt.getList("Skills", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < skills.size(); i++) {
            PlayerSkill skill = new PlayerSkill(skills.getCompound(i));
            if (!SKILLS.containsKey(skill.getId())) {
                continue;
            }
            playerSkills.put(skill.getId(), skill);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Level", this.overallLevel);
        nbt.putFloat("LevelProgress", this.levelProgress);
        nbt.putInt("TotalLevelExperience", this.totalLevelExperience);

        NbtList points = new NbtList();
        for (PlayerPoints point : skillPoints.values()) {
            points.add(point.writeDataToNbt());
        }
        nbt.put("SkillPoints", points);

        NbtList skills = new NbtList();
        for (PlayerSkill skill : this.playerSkills.values()) {
            skills.add(skill.writeDataToNbt());
        }
        nbt.put("Skills", skills);
    }

    public Map<Identifier, PlayerSkill> getPlayerSkills() {
        return playerSkills;
    }

    public void setPlayerSkills(Map<Identifier, PlayerSkill> playerSkills) {
        this.playerSkills = playerSkills;
    }

    public void setSkillPoints(Map<Identifier, PlayerPoints> skillPoints) {
        this.skillPoints = skillPoints;
    }

    public void setOverallLevel(int overallLevel) {
        this.overallLevel = overallLevel;
    }

    public int getOverallLevel() {
        return overallLevel;
    }

    public void setTotalLevelExperience(int totalLevelExperience) {
        this.totalLevelExperience = totalLevelExperience;
    }

    public int getTotalLevelExperience() {
        return totalLevelExperience;
    }

    public void setSkillPoints(Identifier id, int skillPoints) {
        this.skillPoints.computeIfAbsent(id, identifier -> new PlayerPoints(identifier, 0))
                .setLevel(skillPoints);
    }

    public PlayerPoints getSkillPoints(Identifier id) {
        return skillPoints.get(id);
    }

    public Map<Identifier, PlayerPoints> getSkillPoints() {
        return skillPoints;
    }

    public void setLevelProgress(float levelProgress) {
        this.levelProgress = levelProgress;
    }

    public float getLevelProgress() {
        return levelProgress;
    }

    public void setSkillLevel(Identifier skillId, int level) {
        this.playerSkills.get(skillId).setLevel(level);
    }

    public int getSkillLevel(Identifier skillId) {
        // Maybe add a containsKey check here
        PlayerSkill ps = this.playerSkills.get(skillId);

        if (ps != null)
            return ps.getLevel();

        return 0;
    }

    public void addExperienceLevels(int levels) {
        this.overallLevel += levels;

        for (SkillPoints sp : POINTS.values()) {
            PlayerPoints pp = this.skillPoints.computeIfAbsent(sp.id(),
                    id -> new PlayerPoints(id, 0));
            pp.setLevel(pp.getLevel() + sp.perLevel() * levels);
        }

        if (this.overallLevel < 0) {
            this.overallLevel = 0;
            this.levelProgress = 0.0F;
            this.totalLevelExperience = 0;
        }
    }

    public boolean isMaxLevel() {
        if (ConfigInit.MAIN.LEVEL.overallMaxLevel > 0) {
            return this.overallLevel >= ConfigInit.MAIN.LEVEL.overallMaxLevel;
        } else {
            int maxLevel = 0;
            for (Skill skill : SKILLS.values()) {
                maxLevel += skill.maxLevel();
            }
            return this.overallLevel >= maxLevel;
        }
    }

    // Recommend to use https://www.geogebra.org/graphing
    public int getNextLevelExperience() {
        if (isMaxLevel()) {
            return 0;
        }
        int experienceCost = (int) (ConfigInit.MAIN.EXPERIENCE.xpBaseCost + ConfigInit.MAIN.EXPERIENCE.xpCostMultiplicator * Math.pow(this.overallLevel, ConfigInit.MAIN.EXPERIENCE.xpExponent));
        if (ConfigInit.MAIN.EXPERIENCE.xpMaxCost != 0) {
            return experienceCost >= ConfigInit.MAIN.EXPERIENCE.xpMaxCost ? ConfigInit.MAIN.EXPERIENCE.xpMaxCost : experienceCost;
        } else {
            return experienceCost;
        }
    }

    // block
    public boolean hasRequiredBlockLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredBlockLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (BLOCK_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = BLOCK_RESTRICTIONS.get(itemId);
            return playerRestriction.skillLevelRestrictions();
        }
        //return Map.of(0, 0);
        return null;
    }

    // crafting
    public boolean hasRequiredCraftingLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredCraftingLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (CRAFTING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = CRAFTING_RESTRICTIONS.get(itemId);
            return playerRestriction.skillLevelRestrictions();
        }
        return null;
    }

    // entity
    public boolean hasRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = Registries.ENTITY_TYPE.getRawId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredEntityLevel(EntityType<?> entityType) {
        int entityId = Registries.ENTITY_TYPE.getRawId(entityType);
        if (ENTITY_RESTRICTIONS.containsKey(entityId)) {
            PlayerRestriction playerRestriction = ENTITY_RESTRICTIONS.get(entityId);
            return playerRestriction.skillLevelRestrictions();
        }
        return null;
    }

    // item
    public boolean hasRequiredItemLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredItemLevel(Item item) {
        int itemId = Registries.ITEM.getRawId(item);
        if (ITEM_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = ITEM_RESTRICTIONS.get(itemId);
            return playerRestriction.skillLevelRestrictions();
        }
        return null;
    }

    // mining
    public boolean hasRequiredMiningLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredMiningLevel(Block block) {
        int itemId = Registries.BLOCK.getRawId(block);
        if (MINING_RESTRICTIONS.containsKey(itemId)) {
            PlayerRestriction playerRestriction = MINING_RESTRICTIONS.get(itemId);
            return playerRestriction.skillLevelRestrictions();
        }
        return null;
    }

    // enchantment
    public boolean hasRequiredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, int level) {
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
                if (this.getSkillLevel(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<Identifier, Integer> getRequiredEnchantmentLevel(RegistryEntry<Enchantment> enchantment, int level) {
        int enchantmentId = EnchantmentRegistry.getId(enchantment, level);
        if (ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
            PlayerRestriction playerRestriction = ENCHANTMENT_RESTRICTIONS.get(enchantmentId);
            return playerRestriction.skillLevelRestrictions();
        }
        return null;
    }

    public boolean resetSkill(Identifier skillId) {
        PlayerSkill playerSkill = this.playerSkills.get(skillId);

        if (playerSkill == null || playerSkill.getLevel() <= 0) return false;

        int level = playerSkill.getLevel();

        SkillPoints points = playerSkill.skill().get().points().get();
        PlayerPoints pp = this.skillPoints.get(points.id());

        pp.setLevel(points.perLevel() * level + points.start());

        this.setSkillLevel(skillId, 0);
        PacketHelper.updatePlayerSkills((ServerPlayerEntity) this.playerEntity, null);
        LevelHelper.updateSkill((ServerPlayerEntity) this.playerEntity, SKILLS.get(skillId));
        return true;
    }
}
