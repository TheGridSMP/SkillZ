package net.skillz.init;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.skillz.config.ClientConfig;
import net.skillz.config.MainConfig;

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