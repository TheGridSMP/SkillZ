package net.levelz.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.levelz.LevelzMain;
import net.levelz.data.RestrictionLoader;
import net.levelz.data.SkillLoader;
import net.levelz.util.PacketHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;

public class LoaderInit {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SkillLoader());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new RestrictionLoader());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
            if (success) {
                for (int i = 0; i < server.getPlayerManager().getPlayerList().size(); i++) {
                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayerList().get(i);
                    PacketHelper.updateSkills(serverPlayerEntity);
                    PacketHelper.updatePlayerSkills(serverPlayerEntity, null);
                }
                LevelzMain.LOGGER.info("Finished reload on {}", Thread.currentThread());
            } else {
                LevelzMain.LOGGER.error("Failed to reload on {}", Thread.currentThread());
            }
        });
    }

}
