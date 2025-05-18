package net.skillz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.skillz.init.*;
import net.skillz.network.LevelServerPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkillZMain implements ModInitializer {

    public static final String MOD_ID = "skillz";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        CriteriaInit.init();
        EntityInit.init();
        EventInit.init();
        LoaderInit.init();
        LevelServerPacket.init();
        TagInit.init();
        ItemInit.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                Registries.ITEM.forEach(item -> item.getRegistryEntry().getKey().ifPresent(
                        key -> System.out.println(key.getValue()))));
    }

    public static Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }
}
