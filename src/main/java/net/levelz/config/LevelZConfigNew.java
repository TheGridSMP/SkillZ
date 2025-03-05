package net.levelz.config;


import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import net.levelz.LevelzMain;
import net.minecraft.util.Identifier;


public class LevelZConfigNew extends Config {

    public LevelZConfigNew() {
        super(LevelzMain.identifierOf("levelz_config"));
    }

}