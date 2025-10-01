package net.skillz.init;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.skillz.config.ClientConfig;
import net.skillz.config.MainConfig;

public class ConfigInit {

    public static final MainConfig MAIN = ConfigApiJava.registerAndLoadConfig(MainConfig::new);
    public static final ClientConfig CLIENT = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);

    public static void init() { }
}