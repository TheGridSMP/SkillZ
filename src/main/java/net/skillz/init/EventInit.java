package net.skillz.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import net.skillz.level.Skill;
import net.skillz.mixin.entity.EntityAccessor;
import net.skillz.util.LevelHelper;
import net.skillz.util.PacketHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;

public class EventInit {

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            for (Skill skill : LevelManager.SKILLS.values()) {
                LevelHelper.updateSkill(handler.getPlayer(), skill);
            }
            PacketHelper.syncEnchantments(handler.getPlayer());
            PacketHelper.updateSkills(handler.getPlayer());
            PacketHelper.updatePlayerSkills(handler.getPlayer(), null);
            PacketHelper.updateRestrictions(handler.getPlayer());
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            PacketHelper.updatePlayerSkills(player, null);
            PacketHelper.updateLevels(player);
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (alive) {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);
                PacketHelper.updateLevels(newPlayer);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (ConfigInit.MAIN.LEVEL.hardMode) {
                newPlayer.getServer().getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, newPlayer));
                newPlayer.getScoreboard().forEachScore(CriteriaInit.SKILLZ, newPlayer.getEntityName(), ScoreboardPlayerScore::clearScore);
            } else {
                PacketHelper.updatePlayerSkills(newPlayer, oldPlayer);

                if (ConfigInit.MAIN.EXPERIENCE.resetCurrentXp) {
                    LevelManager levelManager = ((LevelManagerAccess) newPlayer).skillz$getLevelManager();
                    levelManager.setLevelProgress(0);
                    levelManager.setTotalLevelExperience(0);
                }

                PacketHelper.updateLevels(newPlayer);
                for (Skill skill : LevelManager.SKILLS.values()) {
                    LevelHelper.updateSkill(newPlayer, skill);
                }
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
                if (!levelManager.hasRequiredItemLevel(player.getStackInHand(hand).getItem())) {
                    System.out.println(levelManager.getRequiredItemLevel(player.getStackInHand(hand).getItem()));
                    //player.sendMessage(Text.literal(customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED), true);
                    player.sendMessage(Text.translatable("restriction.skillz.locked.tooltip").formatted(Formatting.RED), true);
                    return TypedActionResult.fail(player.getStackInHand(hand));
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        UseBlockCallback.EVENT.register((player, world, hand, result) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                BlockPos blockPos = result.getBlockPos();
                if (world.canPlayerModifyAt(player, blockPos)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
                    if (!levelManager.hasRequiredBlockLevel(world.getBlockState(blockPos).getBlock())) {
                        player.sendMessage(Text.translatable("restriction.skillz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.success(false);
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (!player.isCreative() && !player.isSpectator()) {
                if (!entity.hasControllingPassenger() || !((EntityAccessor) entity).callCanAddPassenger(player)) {
                    LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
                    if (!levelManager.hasRequiredEntityLevel(entity.getType())) {
                        player.sendMessage(Text.translatable("restriction.skillz.locked.tooltip").formatted(Formatting.RED), true);
                        return ActionResult.success(false);
                    }
                }
            }
            return ActionResult.PASS;
        });
    }

}
