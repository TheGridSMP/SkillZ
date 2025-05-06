package net.skillz.init;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import ht.treechop.api.TreeChopEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.skillz.SkillZMain;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;

public class CompatInit {

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            Placeholders.register(SkillZMain.identifierOf("playerlevel"), (ctx, arg) -> {
                if (ctx.hasPlayer()) {
                    return PlaceholderResult.value(Integer.toString(((LevelManagerAccess) ctx.player()).skillz$getLevelManager().getOverallLevel()));
                } else {
                    return PlaceholderResult.invalid("No player!");
                }
            });
        }
        if (FabricLoader.getInstance().isModLoaded("treechop")) {
            TreeChopEvents.BEFORE_CHOP.register((world, player, pos, state, chopData) -> {
                LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
                if (!levelManager.hasRequiredItemLevel(player.getMainHandStack().getItem())) {
                    player.getWorld().breakBlock(pos, false);
                    return false;
                } else if (!levelManager.hasRequiredMiningLevel(world.getBlockState(pos).getBlock())) {
                    player.getWorld().breakBlock(pos, false);
                    return false;
                }
                return true;
            });
        }
    }
}
