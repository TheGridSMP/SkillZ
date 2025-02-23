package net.levelz.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.levelz.level.restriction.PlayerRestriction;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Debug(export = true)
@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackClientMixin {

    /*@ModifyArg(method = "method_17869", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static Object appendEnchantments(Object og, @Local NbtCompound nbt, @Local Enchantment e) {
        MutableText text = ((Text)og).copy();
        MutableText newText = Text.literal("(help 1)").formatted(Formatting.RED);
        return text.append(ScreenTexts.SPACE).append(newText);
    }*/

    @Unique
    private static void appendEnchantments(List<Text> tooltip, NbtList enchantments, PlayerEntity player) {
        for (int i = 0; i < enchantments.size(); i++) {
            NbtCompound nbtCompound = enchantments.getCompound(i);
            if (player != null) {
                LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
                Optional<Enchantment> ench = Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound));
                if (ench.isPresent()) {
                    /*if (LevelManager.ENCHANTMENT_RESTRICTIONS.containsKey(itemId)) {
                        PlayerRestriction playerRestriction = LevelManager.ITEM_RESTRICTIONS.get(itemId);
                        lines.add(Text.translatable("restriction.levelz.usable.tooltip"));
                        for (Map.Entry<Integer, Integer> entry : playerRestriction.getSkillLevelRestrictions().entrySet()) {
                            if (isCreative || levelManager.getSkillLevel(entry.getKey()) < entry.getValue()) {
                                lines.add(Text.translatable("restriction.levelz." + LevelManager.SKILLS.get(entry.getKey()).getKey() + ".tooltip", entry.getValue()).formatted(Formatting.RED));
                            }
                        }
                    }*/
                    if (levelManager.hasRequiredEnchantmentLevel(Registries.ENCHANTMENT.getEntry(ench.get()), EnchantmentHelper.getLevelFromNbt(nbtCompound))) {
                        System.out.println("HAS LEVEL");
                        Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> tooltip.add(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound))));
                    }else {
                        //levelManager.getRequiredEnchantmentLevel()
                        Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound))
                                .ifPresent(e -> tooltip.add( e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound)).copy().append(ScreenTexts.SPACE).append(Text.literal("(help 1)").formatted(Formatting.RED)) ));
                    }
                }
            }else {
                Registries.ENCHANTMENT.getOrEmpty(EnchantmentHelper.getIdFromNbt(nbtCompound)).ifPresent(e -> tooltip.add(e.getName(EnchantmentHelper.getLevelFromNbt(nbtCompound))));
            }
        }
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V"))
    public void test(List<Text> tooltip, NbtList enchantments, PlayerEntity player) {
        appendEnchantments(tooltip, enchantments, player);
    }

}
