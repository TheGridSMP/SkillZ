package net.skillz;

import net.fabricmc.api.ClientModInitializer;
import net.skillz.init.KeyInit;
import net.skillz.init.RenderInit;
import net.skillz.network.LevelClientPacket;

public class SkillZClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyInit.init();
        LevelClientPacket.init();
        RenderInit.init();
    }

}