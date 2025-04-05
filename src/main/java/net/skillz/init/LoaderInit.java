package net.skillz.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.skillz.SkillZMain;
import net.skillz.data.RestrictionLoader;
import net.skillz.data.SkillLoader;
import net.skillz.util.PacketHelper;
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
                SkillZMain.LOGGER.info("Finished reload on {}", Thread.currentThread());
            } else {
                SkillZMain.LOGGER.error("Failed to reload on {}", Thread.currentThread());
            }
        });
    }

}
