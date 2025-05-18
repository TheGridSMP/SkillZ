package net.skillz.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.skillz.access.LevelManagerAccess;
import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.level.*;
import net.skillz.registry.EnchantmentRegistry;
import net.skillz.registry.EnchantmentZ;
import net.skillz.screen.LevelScreen;
import net.skillz.network.packet.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class LevelClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(OrbPacket.PACKET_ID, (client, handler, buf, responseSender) -> {
            OrbPacket packet = new OrbPacket(buf);
            double d = packet.getX();
            double e = packet.getY();
            double f = packet.getZ();
            Entity entity = new LevelExperienceOrbEntity(handler.getWorld(), d, e, f, packet.getExperience());
            entity.updateTrackedPosition(d, e, f);
            entity.setYaw(0.0F);
            entity.setPitch(0.0F);
            entity.setId(packet.getEntityId());
            handler.getWorld().addEntity(entity.getId(), entity);
        });

        ClientPlayNetworking.registerGlobalReceiver(SkillSyncPacket.TYPE, (packet, player, responseSender) -> {
            LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();

            LevelManager.POINTS.clear();
            for (SkillPoints points : packet.skillPoints()) {
                LevelManager.POINTS.put(points.id(), points);
            }

            LevelManager.SKILLS.clear();
            for (Skill skill : packet.skills()) {
                LevelManager.SKILLS.put(skill.id(), skill);

                if (!levelManager.getPlayerSkills().containsKey(skill.id())) {
                    PlayerSkill playerSkill = new PlayerSkill(skill.id(), 0);
                    levelManager.getPlayerSkills().put(skill.id(), playerSkill);
                }
            }

            LevelManager.BONUSES.clear();
            for (SkillBonus bonus : packet.skillBonuses()) {
                LevelManager.BONUSES.put(bonus.id(), bonus);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(PlayerSkillSyncPacket.TYPE, (packet, player, responseSender) -> {
            LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
            for (PlayerSkill skill : packet.playerSkills()) {
                levelManager.setSkillLevel(skill.getId(), skill.getLevel());
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(LevelPacket.PACKET_ID, (client, handler, buf, sender) -> {
            LevelPacket payload = new LevelPacket(buf);
            int overallLevel = payload.overallLevel();
            Map<Identifier, PlayerPoints> skillPoints = payload.skillPoints();
            int totalLevelExperience = payload.totalLevelExperience();
            float levelProgress = payload.levelProgress();
            client.execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) client.player).skillz$getLevelManager();
                levelManager.setOverallLevel(overallLevel);
                levelManager.setSkillPoints(skillPoints);
                levelManager.setTotalLevelExperience(totalLevelExperience);
                levelManager.setLevelProgress(levelProgress);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RestrictionPacket.PACKET_ID,  (client, handler, buf, sender) -> {
            RestrictionPacket payload = new RestrictionPacket(buf);
            RestrictionPacket.RestrictionRecord blockRestrictions = payload.blockRestrictions();
            RestrictionPacket.RestrictionRecord craftingRestrictions = payload.craftingRestrictions();
            RestrictionPacket.RestrictionRecord entityRestrictions = payload.entityRestrictions();
            RestrictionPacket.RestrictionRecord itemRestrictions = payload.itemRestrictions();
            RestrictionPacket.RestrictionRecord miningRestrictions = payload.miningRestrictions();
            RestrictionPacket.RestrictionRecord enchantmentRestrictions = payload.enchantmentRestrictions();

            client.execute(() -> {
                LevelManager.BLOCK_RESTRICTIONS.clear();
                LevelManager.CRAFTING_RESTRICTIONS.clear();
                LevelManager.ENTITY_RESTRICTIONS.clear();
                LevelManager.ITEM_RESTRICTIONS.clear();
                LevelManager.MINING_RESTRICTIONS.clear();
                LevelManager.ENCHANTMENT_RESTRICTIONS.clear();

                blockRestrictions.forEach(LevelManager.BLOCK_RESTRICTIONS::put);
                craftingRestrictions.forEach(LevelManager.CRAFTING_RESTRICTIONS::put);
                entityRestrictions.forEach(LevelManager.ENTITY_RESTRICTIONS::put);
                itemRestrictions.forEach(LevelManager.ITEM_RESTRICTIONS::put);
                miningRestrictions.forEach(LevelManager.MINING_RESTRICTIONS::put);
                enchantmentRestrictions.forEach(LevelManager.ENCHANTMENT_RESTRICTIONS::put);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(StatPacket.TYPE, (packet, player, responseSender) -> {
            Identifier id = packet.skillId();
            int level = packet.level();

            LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
            levelManager.setSkillLevel(id, level);

            if (MinecraftClient.getInstance().currentScreen instanceof LevelScreen levelScreen)
                levelScreen.updateLevelButtons();
        });

        ClientPlayNetworking.registerGlobalReceiver(EnchantmentZPacket.PACKET_ID, (client, handler, buf, sender) -> {
            EnchantmentZPacket payload = new EnchantmentZPacket(buf);
            Map<String, Integer> indexed = payload.indexed();
            List<Integer> keys = payload.keys();
            List<String> ids = payload.ids();
            List<Integer> levels = payload.levels();
            client.execute(() -> {
                EnchantmentRegistry.ENCHANTMENTS.clear();
                EnchantmentRegistry.INDEX_ENCHANTMENTS.clear();

                Registry<Enchantment> registry = client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);

                for (int i = 0; i < keys.size(); i++) {
                    int key = keys.get(i);

                    Identifier id = new Identifier(ids.get(i));
                    RegistryEntry<Enchantment> entry = registry.getEntry(registry.get(id));

                    int level = levels.get(i);
                    EnchantmentRegistry.ENCHANTMENTS.put(key, new EnchantmentZ(entry, level));
                }

                EnchantmentRegistry.INDEX_ENCHANTMENTS.putAll(indexed);
            });
        });
    }
}

