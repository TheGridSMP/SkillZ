package net.levelz.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.LevelzMain;
import net.levelz.level.LevelManager;
import net.levelz.screen.SkillRestrictionScreen;
import net.levelz.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class BookWidget {

    public static final Identifier BOOK_TEXTURE = LevelzMain.identifierOf("textures/gui/book_template.png");
    public final Text title;
    public Color color;
    public final boolean context;
    public final int x;
    public final int y;
    public final Runnable runnable;
    //public final Screen screen;

    public BookWidget(Text title, int x, int y, Runnable runnable, Color color, boolean context) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.runnable = runnable;
        this.color = color;
        this.context = context;
    }

    public void draw(TextRenderer textRenderer, DrawContext context, int mouseX, int mouseY) {
        if (!this.context) {
            color = new Color(200,200,200);
        }
        if (this.context && this.isHovered(mouseX, mouseY)) {
            context.drawTexture(BOOK_TEXTURE, x, y, 15, 13, 15, 13, 45, 26);
            context.drawTooltip(textRenderer, this.title, mouseX, mouseY);
        } else {
            setColor(context, color.darker().darker().darker().getRGB());
            context.drawTexture(BOOK_TEXTURE, x, y, 15, 0, 15, 13, 45, 26);
        }
        setColor(context, -1);

        setColor(context, color.getRGB());
        context.drawTexture(BOOK_TEXTURE, x, y, 0, 0, 15, 13, 45, 26);
        setColor(context, -1);

        context.drawTexture(BOOK_TEXTURE, x, y, 30, 13, 15, 13, 45, 26);
        setColor(context, -1);
    }

    public void click(MinecraftClient client, LevelManager levelManager, double mouseX, double mouseY) {
        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        //client.setScreen(this.screen);
        runnable.run();
    }

    public void setColor(DrawContext context, int hex) {
        context.setShaderColor(ColorHelper.Argb.getRed(hex) / 255.0F, ColorHelper.Argb.getGreen(hex) / 255.0F, ColorHelper.Argb.getBlue(hex) / 255.0F, ColorHelper.Argb.getAlpha(hex) / 255.0F);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return DrawUtil.isPointWithinBounds(x, y, 14, 13, mouseX, mouseY);
    }

}
