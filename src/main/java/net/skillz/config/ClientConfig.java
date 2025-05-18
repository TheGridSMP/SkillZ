package net.skillz.config;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.skillz.SkillZMain;

public class ClientConfig extends Config {

    public ClientConfig() {
        super(SkillZMain.id("client_config"));
    }

    @Comment("Hide reached levels in tooltips")
    public boolean hideReachedLevels = true;

    @Comment("How locked blocks should appear highlighted")
    public BlockHighlightOption highlightOption = BlockHighlightOption.NORMAL;

    public int inventorySkillLevelPosX = 0;

    public int inventorySkillLevelPosY = 0;

    @RequiresAction(action = Action.RESTART)
    public boolean showLevelList = true;

    public boolean showLevel = true;

    @Comment("Inventory id goes back to main screen rather than closing the inventory")
    public boolean switchScreen = false;

    public boolean showLockedBlockInfo = false;

    public int lockedBlockInfoPosX = 0;

    public int lockedBlockInfoPosY = 0;

    public enum BlockHighlightOption {
        NORMAL,
        RED,
        NONE;
    }
}
