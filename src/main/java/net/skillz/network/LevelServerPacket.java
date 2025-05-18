package net.skillz.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.skillz.access.LevelManagerAccess;
import net.skillz.init.ConfigInit;
import net.skillz.init.CriteriaInit;
import net.skillz.level.LevelManager;
import net.skillz.level.PlayerSkill;
import net.skillz.level.Skill;
import net.skillz.network.packet.*;
import net.skillz.util.LevelHelper;
import net.skillz.util.PacketHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;

import java.util.List;

public class LevelServerPacket {

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(StatPacket.PACKET_ID, (server, player, handler, buffer, sender)  -> {
            StatPacket payload = new StatPacket(buffer);
            Identifier skillId = payload.skillId();
            int level = payload.level();

            server.execute(() -> {
                LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();

                if (ConfigInit.MAIN.LEVEL.overallMaxLevel > 0 && ConfigInit.MAIN.LEVEL.overallMaxLevel <= levelManager.getOverallLevel())
                    return;

                Skill skill = LevelManager.SKILLS.get(skillId);

                Identifier pointId = skill.pointsId();
                int points = levelManager.getSkillPoints(pointId).getLevel();

                if (points - level >= 0) {
                    PlayerSkill playerSkill = levelManager.getPlayerSkills().get(skillId);

                    if (!ConfigInit.MAIN.LEVEL.allowHigherSkillLevel && playerSkill.getLevel() >= skill.maxLevel())
                        return;

                    for (Skill skillCheck : LevelManager.SKILLS.values()) {
                        if (skillCheck.maxLevel() > levelManager.getSkillLevel(skillCheck.id())) {
                            return;
                        }
                    }

                    for (int i = 1; i <= level; i++) {
                        CriteriaInit.SKILL_UP.trigger(player, skill.id(), playerSkill.getLevel() + level);
                    }

                    levelManager.setSkillLevel(skillId, playerSkill.getLevel() + level);
                    levelManager.setSkillPoints(pointId, points - level);
                    LevelHelper.updateSkill(player, skill);
                    PacketHelper.updateLevels(player);

                    ServerPlayNetworking.send(player, new StatPacket(skillId, levelManager.getSkillLevel(skillId)));
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(AttributeSyncPacket.PACKET_ID, (server, player, handler, buffer, sender) -> server.execute(() -> {
            // Following are already synced
            // Collection<EntityAttributeInstance> collection = context.player().getAttributes().getAttributesToSend();
            // context.player().networkHandler.sendPacket(new EntityAttributesS2CPacket(context.player().getId(), collection));
            // Is required lul
            player.networkHandler.sendPacket(new EntityAttributesS2CPacket(player.getId(), List.of(player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))));
        }));
    }
}
