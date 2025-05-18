package net.skillz.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.util.Identifier;
import net.skillz.SkillZMain;
import net.skillz.access.LevelManagerAccess;
import net.skillz.access.ServerPlayerSyncAccess;
import net.skillz.level.LevelManager;
import net.skillz.level.Skill;
import net.skillz.level.SkillPoints;
import net.skillz.util.LevelHelper;
import net.skillz.util.PacketHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandInit {

    private static final SuggestionProvider<ServerCommandSource> SKILLS_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
            LevelManager.SKILLS.values().stream().map(Skill::id).map(Identifier::toString), builder);

    private static final SuggestionProvider<ServerCommandSource> POINTS_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
            LevelManager.POINTS.values().stream().map(SkillPoints::id).map(Identifier::toString), builder);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) ->
                dispatcher.register(literal(SkillZMain.MOD_ID).requires((source) -> source.hasPermissionLevel(2))
                        .then(argument("targets", EntityArgumentType.players())
                // Add values
                .then(literal("add")
                        .then(literal("level").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::addLevel)))

                        .then(literal("pointsId").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::addPoints))))

                        .then(literal("skill").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .suggests(SKILLS_SUGGESTION_PROVIDER).then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::addSkill))))

                        .then(literal("experience").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::addXp))))

                // Remove values
                .then(literal("remove")
                        .then(literal("level").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::removeLevel)))

                        .then(literal("pointsId").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::removePoints))))

                        .then(literal("skill").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .suggests(SKILLS_SUGGESTION_PROVIDER).then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::removeSkill))))

                        .then(literal("experience").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::removeXp))))

                // Set values
                .then(literal("set")
                        .then(literal("level").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::setLevel)))

                        .then(literal("pointsId").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::setPoints))))

                        .then(literal("skill").then(argument("skillKey", IdentifierArgumentType.identifier())
                                .suggests(SKILLS_SUGGESTION_PROVIDER).then(argument("level", IntegerArgumentType.integer())
                                        .executes(CommandInit::setSkill))))

                        .then(literal("experience").then(argument("level", IntegerArgumentType.integer())
                                .executes(CommandInit::setXp))))

        )));
    }

    private static int addSkill(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier skillId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeSkill(ctx, targets, skillId, Operation.ADD, amount);
    }

    private static int removeSkill(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier skillId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeSkill(ctx, targets, skillId, Operation.REMOVE, amount);
    }

    private static int setSkill(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier skillId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeSkill(ctx, targets, skillId, Operation.SET, amount);
    }

    private static int changeSkill(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, Identifier skillId, Operation op, int amount) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            changeSkill(ctx.getSource(), serverPlayerEntity, skillId, op, amount);
        }

        return targets.size();
    }

    private static void changeSkill(ServerCommandSource source, ServerPlayerEntity player, Identifier skillId, Operation op, int amount) {
        LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
        Skill skill = LevelManager.SKILLS.get(skillId);

        if (skill == null) {
            source.sendFeedback(() -> Text.translatable("commands.level.failed"), false);
            return;
        }

        int level = op.compute(levelManager.getSkillLevel(skillId), amount);
        levelManager.setSkillLevel(skill.id(), level);

        if (!skill.attributes().isEmpty()) LevelHelper.updateSkill(player, skill);

        PacketHelper.updateLevels(player);
        PacketHelper.updatePlayerSkills(player, null);

        source.sendFeedback(() -> Text.translatable("commands.level.changed", player.getDisplayName()), true);
    }

    enum Operation {
        ADD,
        GET,
        SET,
        REMOVE;

        public int compute(int level, int amount) {
            return switch (this) {
                case ADD -> level + amount;
                case REMOVE -> Math.max(level - amount, 0);
                case SET -> amount;
                default -> throw new IllegalStateException("Unreachable.");
            };
        }
    }

    private static int addXp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeXp(ctx, targets, Operation.ADD, amount);
    }

    private static int removeXp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeXp(ctx, targets, Operation.REMOVE, amount);
    }

    private static int setXp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeXp(ctx, targets, Operation.SET, amount);
    }

    private static int changeXp(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, Operation op, int amount) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            changeXp(ctx.getSource(), serverPlayerEntity, op, amount);
        }

        return targets.size();
    }

    private static void changeXp(ServerCommandSource source, ServerPlayerEntity player, Operation op, int amount) {
        LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();

        switch (op) {
            case ADD -> ((ServerPlayerSyncAccess) player).skillz$addLevelExperience(amount);

            case SET -> {
                float oldProgress = levelManager.getLevelProgress();
                levelManager.setLevelProgress(amount >= levelManager.getNextLevelExperience() ? 1.0F : (float) amount / levelManager.getNextLevelExperience());
                levelManager.setTotalLevelExperience((int) (levelManager.getTotalLevelExperience() - oldProgress * levelManager.getNextLevelExperience()
                        + levelManager.getLevelProgress() * levelManager.getNextLevelExperience()));
            }

            case REMOVE -> {
                int currentXP = (int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience());
                float oldProgress = levelManager.getLevelProgress();
                levelManager.setLevelProgress(currentXP - amount > 0 ? (float) (currentXP - 1) / (float) levelManager.getNextLevelExperience() : 0.0F);
                levelManager.setTotalLevelExperience(currentXP - amount > 0 ? levelManager.getTotalLevelExperience() - amount
                        : levelManager.getTotalLevelExperience() - (int) (oldProgress * levelManager.getNextLevelExperience()));
            }
        }

        PacketHelper.updateLevels(player);
        PacketHelper.updatePlayerSkills(player, null);

        source.sendFeedback(() -> Text.translatable("commands.level.changed", player.getDisplayName()), true);
    }

    // LEVELS

    private static int addLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeLevel(ctx, targets, Operation.ADD, amount);
    }

    private static int removeLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeLevel(ctx, targets, Operation.REMOVE, amount);
    }

    private static int setLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changeLevel(ctx, targets, Operation.SET, amount);
    }

    private static int changeLevel(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, Operation op, int amount) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            changeLevel(ctx.getSource(), serverPlayerEntity, op, amount);
        }

        return targets.size();
    }

    private static void changeLevel(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity, Operation op, int amount) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).skillz$getLevelManager();
        int level = op.compute(levelManager.getOverallLevel(), amount);

        levelManager.setOverallLevel(level);
        serverPlayerEntity.getScoreboard().forEachScore(CriteriaInit.SKILLZ, serverPlayerEntity.getEntityName(), score -> score.setScore(level));
        serverPlayerEntity.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, serverPlayerEntity));

        PacketHelper.updateLevels(serverPlayerEntity);
        PacketHelper.updatePlayerSkills(serverPlayerEntity, null);

        source.sendFeedback(() -> Text.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
    }

    // Points

    private static int addPoints(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier pointId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changePoints(ctx, targets, pointId, Operation.ADD, amount);
    }

    private static int removePoints(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier pointId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changePoints(ctx, targets, pointId, Operation.REMOVE, amount);
    }

    private static int setPoints(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
        Identifier pointId = IdentifierArgumentType.getIdentifier(ctx, "skillKey");
        int amount = IntegerArgumentType.getInteger(ctx, "level");

        return changePoints(ctx, targets, pointId, Operation.SET, amount);
    }

    private static int changePoints(CommandContext<ServerCommandSource> ctx, Collection<ServerPlayerEntity> targets, Identifier skillId, Operation op, int amount) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            changePoints(ctx.getSource(), serverPlayerEntity, skillId, op, amount);
        }

        return targets.size();
    }

    private static void changePoints(ServerCommandSource source, ServerPlayerEntity serverPlayerEntity, Identifier pointId, Operation op, int amount) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).skillz$getLevelManager();
        int playerSkillLevel = op.compute(levelManager.getSkillPoints(pointId).getLevel(), amount);

        levelManager.setSkillPoints(pointId, playerSkillLevel);

        PacketHelper.updateLevels(serverPlayerEntity);
        PacketHelper.updatePlayerSkills(serverPlayerEntity, null);

        source.sendFeedback(() -> Text.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
    }
}