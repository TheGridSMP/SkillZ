package net.levelz.screen.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.levelz.LevelzMain;
import net.levelz.util.DrawUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class BookWidget {

    public static final Identifier BOOK_TEXTURE = LevelzMain.identifierOf("textures/gui/book_template.png");
    public final Text title;
    public final Color color;

    public BookWidget(Text title, Color color) {
        this.title = title;
        this.color = color;
    }

    public void draw(TextRenderer textRenderer, DrawContext context, int mouseX, int mouseY, int x, int y) {
        if (DrawUtil.isPointWithinBounds(x, y, 14, 13, mouseX, mouseY)) {
            context.drawTexture(BOOK_TEXTURE, x, y, 15, 13, 15, 13, 45, 26);
            context.drawTooltip(textRenderer, Text.translatable("restriction.levelz.mining"), mouseX, mouseY);
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

    public void setColor(DrawContext context, int hex) {
        context.setShaderColor(ColorHelper.Argb.getRed(hex) / 255.0F, ColorHelper.Argb.getGreen(hex) / 255.0F, ColorHelper.Argb.getBlue(hex) / 255.0F, ColorHelper.Argb.getAlpha(hex) / 255.0F);
    }

}
