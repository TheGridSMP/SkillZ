package net.levelz.util;

import net.levelz.LevelzMain;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.Skill;
import net.levelz.level.SkillAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class LevelHelper {

    public static void updateSkill(ServerPlayerEntity serverPlayerEntity, Skill skill) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).getLevelManager();
        for (SkillAttribute skillAttribute : skill.attributes()) {
            EntityAttributeInstance attr = serverPlayerEntity.getAttributeInstance(skillAttribute.getAttribute().value());
            if (attr != null) {
                if (skillAttribute.getBaseValue() > -9999.0f) {
                    attr.setBaseValue(skillAttribute.getBaseValue());
                }
                Identifier identifier = LevelzMain.identifierOf(skill.key());
                UUID uid = UUID.nameUUIDFromBytes(identifier.toString().getBytes());
                if (attr.getModifier(uid) != null && attr.hasModifier(attr.getModifier(uid))) {
                    attr.removeModifier(uid);
                }
                attr.addTemporaryModifier(new EntityAttributeModifier(uid, identifier.toString(), skillAttribute.getLevelValue() * levelManager.getSkillLevel(skill.id()), skillAttribute.getOperation()));
            }
        }
    }
}
