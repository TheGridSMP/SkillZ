package net.levelz.init;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.levelz.config.ClientConfig;
import net.levelz.config.LevelZConfigNew;
import net.levelz.config.MainConfig;

public class ConfigInit {

    /*public static final boolean isOriginsLoaded = FabricLoader.getInstance().isModLoaded("origins");

    public static LevelzConfig CONFIG = new LevelzConfig();*/

    public static MainConfig MAIN = ConfigApiJava.registerAndLoadConfig(MainConfig::new);
    public static ClientConfig CLIENT = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);

    public static void init() {
        /*AutoConfig.register(LevelzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LevelzConfig.class).getConfig();*/
    }

}