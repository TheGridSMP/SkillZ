package net.levelz;

import net.fabricmc.api.ClientModInitializer;
import net.levelz.init.KeyInit;
import net.levelz.init.RenderInit;
import net.levelz.network.LevelClientPacket;

public class LevelzClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyInit.init();
        LevelClientPacket.init();
        RenderInit.init();
    }

}