package net.levelz.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.screen.LevelScreen;
import net.levelz.screen.SkillInfoScreen;
import net.levelz.screen.SkillRestrictionScreen;
import dev.sygii.tabapi.api.InventoryTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LevelzTab extends InventoryTab {

    public LevelzTab(Text title, Identifier texture, int preferedPos, Class<?>... screenClasses) {
        super(title, texture, preferedPos, screenClasses);
    }

    @Override
    public boolean canClick(Screen screen, MinecraftClient client) {
        if (screen.getClass().equals(SkillInfoScreen.class) || screen.getClass().equals(SkillRestrictionScreen.class)) {
            return true;
        }
        return super.canClick(screen, client);
    }

    @Override
    public void onClick(MinecraftClient client) {
        client.setScreen(new LevelScreen());
    }

}
