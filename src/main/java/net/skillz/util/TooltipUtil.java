package net.skillz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.skillz.access.LevelManagerAccess;
import net.skillz.init.ConfigInit;
import net.skillz.level.LevelManager;
import net.skillz.level.restriction.PlayerRestriction;
import net.skillz.registry.EnchantmentRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class TooltipUtil {

    public static void shit(Int2ObjectMap<PlayerRestriction> restrictions, List<Text> lines, String name, int id, boolean isCreative, LevelManager levelManager) {
        PlayerRestriction playerRestriction = restrictions.get(id);

        if (playerRestriction == null)
            return;

        lines.add(Text.translatable("restriction.skillz." + name + ".tooltip"));
        for (Map.Entry<Identifier, Integer> entry : playerRestriction.skillLevelRestrictions().entrySet()) {
            boolean noHasLevel = levelManager.getSkillLevel(entry.getKey()) < entry.getValue();

            if (isCreative || noHasLevel)
                lines.add(Text.translatable(LevelManager.SKILLS.get(entry.getKey()).id().toTranslationKey("restriction", "tooltip"), entry.getValue()).formatted((ConfigInit.CLIENT.hideReachedLevels || noHasLevel) ? Formatting.RED : Formatting.GREEN));
        }
    }

    public static void renderItemTooltip(MinecraftClient client, ItemStack stack, List<Text> lines) {
        if (client.player != null) {
            LevelManager levelManager = ((LevelManagerAccess) client.player).skillz$getLevelManager();
            boolean isCreative = client.player.isCreative() || !ConfigInit.CLIENT.hideReachedLevels; // Add all lines, not only the missing ones

            if (stack.getItem() instanceof BlockItem blockItem) {
                int blockId = Registries.BLOCK.getRawId(blockItem.getBlock());
                if (isCreative || !levelManager.hasRequiredBlockLevel(blockItem.getBlock())) {
                    shit(LevelManager.BLOCK_RESTRICTIONS, lines, "usable", blockId, isCreative, levelManager);
                }
                if (isCreative || !levelManager.hasRequiredMiningLevel(blockItem.getBlock())) {
                    shit(LevelManager.MINING_RESTRICTIONS, lines, "mineable", blockId, isCreative, levelManager);
                }
            }
            int itemId = Registries.ITEM.getRawId(stack.getItem());
            if (isCreative || !levelManager.hasRequiredItemLevel(stack.getItem())) {
                shit(LevelManager.ITEM_RESTRICTIONS, lines, "usable", itemId, isCreative, levelManager);
            }
            if (isCreative || !levelManager.hasRequiredCraftingLevel(stack.getItem())) {
                shit(LevelManager.CRAFTING_RESTRICTIONS, lines, "craftable", itemId, isCreative, levelManager);
            }

            final Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);

            if (!enchantments.isEmpty()) {
                for (Enchantment enchantment : enchantments.keySet()) {
                    final Text fullName = enchantment.getName(enchantments.get(enchantment));
                    for (Text line : lines) {
                        if (line.equals(fullName)) {
                            boolean changed = false;
                            MutableText asd = Text.empty();
                            int count = 0;
                            if (isCreative || !levelManager.hasRequiredEnchantmentLevel(Registries.ENCHANTMENT.getEntry(enchantment), enchantments.get(enchantment))) {
                                int enchantmentId = EnchantmentRegistry.getId(Registries.ENCHANTMENT.getEntry(enchantment), enchantments.get(enchantment));
                                if (LevelManager.ENCHANTMENT_RESTRICTIONS.containsKey(enchantmentId)) {
                                    Map<Identifier, Integer> map = levelManager.getRequiredEnchantmentLevel(Registries.ENCHANTMENT.getEntry(enchantment), enchantments.get(enchantment));
                                    for (Map.Entry<Identifier, Integer> entry : map.entrySet()) {
                                        boolean noHasLevel = levelManager.getSkillLevel(entry.getKey()) < entry.getValue();
                                        if (isCreative || noHasLevel) {
                                            asd.append(Text.translatable(LevelManager.SKILLS.get(entry.getKey()).id().toTranslationKey("restriction", "tooltip"), entry.getValue()).formatted((ConfigInit.CLIENT.hideReachedLevels || noHasLevel) ? Formatting.RED : Formatting.GRAY));
                                            if ((map.size() - count) > 1) {
                                                asd.append(Text.literal(",").formatted(Formatting.GRAY)).append(ScreenTexts.SPACE);
                                            }
                                            changed = true;
                                            count++;
                                        }
                                    }
                                }
                            }
                            
                            if (changed) {
                                lines.set(lines.indexOf(line), line.copy().append(ScreenTexts.SPACE).append(Text.literal("(").formatted(Formatting.GRAY))
                                        .append(asd).append(Text.literal(")").formatted(Formatting.GRAY)));
                            }
                            
                            break;
                        }
                    }
                }
            }
            if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
                if (isCreative || !levelManager.hasRequiredEntityLevel(spawnEggItem.getEntityType(stack.getNbt()))) {
                    int entityId = Registries.ENTITY_TYPE.getRawId(spawnEggItem.getEntityType(stack.getNbt()));
                    shit(LevelManager.ENTITY_RESTRICTIONS, lines, "usable", entityId, isCreative, levelManager);
                }
            }
        }
    }

    public static void renderTooltip(MinecraftClient client, DrawContext context) {
        if (client.crosshairTarget != null && ConfigInit.CLIENT.showLockedBlockInfo) {
            HitResult hitResult = client.crosshairTarget;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                LevelManager levelManager = ((LevelManagerAccess) client.player).skillz$getLevelManager();
                EntityType<?> entityType = ((EntityHitResult) hitResult).getEntity().getType();
                if (!levelManager.hasRequiredEntityLevel(entityType)) {
                    List<Text> textList = new ArrayList<>();
                    textList.add(Text.of(entityType.getName().getString()));
                    for (Map.Entry<Identifier, Integer> entry : levelManager.getRequiredEntityLevel(entityType).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable(LevelManager.SKILLS.get(entry.getKey()).id().toTranslationKey("restriction", "tooltip"), entry.getValue()).formatted(formatting));
                    }
                    renderTooltip(client, context, textList,
                            null, context.getScaledWindowWidth() / 2 + ConfigInit.CLIENT.lockedBlockInfoPosX, ConfigInit.CLIENT.lockedBlockInfoPosY);
                }
            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                Block block = client.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock();
                LevelManager levelManager = ((LevelManagerAccess) client.player).skillz$getLevelManager();
                List<Text> textList = new ArrayList<>();
                if (!levelManager.hasRequiredMiningLevel(block)) {
                    textList.add(Text.of(block.getName().getString()));
                    for (Map.Entry<Identifier, Integer> entry : levelManager.getRequiredMiningLevel(block).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable(LevelManager.SKILLS.get(entry.getKey()).id().toTranslationKey("restriction", "tooltip"), entry.getValue()).formatted(formatting));
                    }
                }
                if (!levelManager.hasRequiredBlockLevel(block)) {
                    if (textList.isEmpty()) {
                        textList.add(Text.of(block.getName().getString()));
                    }
                    textList.add(Text.translatable("restriction.skillz.block_usage"));
                    for (Map.Entry<Identifier, Integer> entry : levelManager.getRequiredBlockLevel(block).entrySet()) {
                        Formatting formatting =
                                levelManager.getSkillLevel(entry.getKey()) < entry.getValue() ? Formatting.RED : Formatting.GREEN;
                        textList.add(Text.translatable(LevelManager.SKILLS.get(entry.getKey()).id().toTranslationKey("restriction", "tooltip"), entry.getValue()).formatted(formatting));
                    }
                }
                if (!textList.isEmpty()) {
                    renderTooltip(client, context, textList,
                            Registries.BLOCK.getId(block), context.getScaledWindowWidth() / 2 + ConfigInit.CLIENT.lockedBlockInfoPosX, ConfigInit.CLIENT.lockedBlockInfoPosY);
                }
            }
        }
    }

    private static void renderTooltip(MinecraftClient client, DrawContext context, List<Text> textList, @Nullable Identifier identifier, int x, int y) {
        int maxTextWidth = 0;
        for (int i = 0; i < textList.size(); i++) {
            if (client.textRenderer.getWidth(textList.get(i)) > maxTextWidth) {
                maxTextWidth = client.textRenderer.getWidth(textList.get(i));
                if (i == 0 && identifier != null) {
                    maxTextWidth += 22;
                }
            }
        }
        maxTextWidth += 5;

        context.getMatrices().push();

        int colorStart = 0xBF191919; // background
        int colorTwo = 0xBF7F0200; // light border
        int colorThree = 0xBF380000; // darker border

        render(context, x - maxTextWidth / 2 - 3, y + 4, maxTextWidth, textList.size() * 10 + 11, 400, colorStart, colorTwo, colorThree);

        context.getMatrices().translate(0.0, 0.0, 400.0);

        int i = 9;
        for (Text text : textList) {
            if (i == 9) {
                context.drawText(client.textRenderer, text, x - maxTextWidth / 2 + (identifier != null ? 20 : 0), y + i, 0xFFFFFF, false);
            } else {
                context.drawText(client.textRenderer, text, x - maxTextWidth / 2, y + i + 8, 0xFFFFFF, false);
            }
            i += 10;
        }

        if (identifier != null) {
            context.drawItem(Registries.ITEM.get(identifier).getDefaultStack(), x - maxTextWidth / 2, y + 5);
        }
        context.getMatrices().pop();
    }

    public static void render(DrawContext context, int x, int y, int width, int height, int z, int background, int borderColorStart, int borderColorEnd) {
        int i = x - 3;
        int j = y - 3;
        int k = width + 3 + 3;
        int l = height + 3 + 3;

        renderHorizontalLine(context, i, j - 1, k, z, background);
        renderHorizontalLine(context, i, j + l, k, z, background);
        renderRectangle(context, i, j, k, l, z, background);
        renderVerticalLine(context, i - 1, j, l, z, background);
        renderVerticalLine(context, i + k, j, l, z, background);
        renderBorder(context, i, j + 1, k, l, z, borderColorStart, borderColorEnd);

        width -= 6;
        renderHorizontalLine(context, z, x + 3, y + 19, x + 3 + width / 2, y + 20, 0x007F0200, 0xBF7F0200);
        renderHorizontalLine(context, z, x + 3 + width / 2, y + 19, x + 3 + width, y + 20, 0xBF7F0200, 0x007F0200);
    }

    private static void renderBorder(DrawContext context, int x, int y, int width, int height, int z, int startColor, int endColor) {
        renderVerticalLine(context, x, y, height - 2, z, startColor, endColor);
        renderVerticalLine(context, x + width - 1, y, height - 2, z, startColor, endColor);
        renderHorizontalLine(context, x, y - 1, width, z, startColor);
        renderHorizontalLine(context, x, y - 1 + height - 1, width, z, endColor);
    }

    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int color) {
        context.fill(x, y, x + 1, y + height, z, color);
    }

    private static void renderVerticalLine(DrawContext context, int x, int y, int height, int z, int startColor, int endColor) {
        context.fillGradient(x, y, x + 1, y + height, z, startColor, endColor);
    }

    private static void renderHorizontalLine(DrawContext context, int x, int y, int width, int z, int color) {
        context.fill(x, y, x + width, y + 1, z, color);
    }

    private static void renderRectangle(DrawContext context, int x, int y, int width, int height, int z, int color) {
        context.fill(x, y, x + width, y + height, z, color);
    }

    public static void renderHorizontalLine(DrawContext context, int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix4f, right, top, zLevel).color(endRed, endGreen, endBlue, endAlpha);
        vertexConsumer.vertex(matrix4f, left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.vertex(matrix4f, left, bottom, zLevel).color(startRed, startGreen, startBlue, startAlpha);
        vertexConsumer.vertex(matrix4f, right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha);
        context.draw();
        RenderSystem.disableBlend();
    }
}
