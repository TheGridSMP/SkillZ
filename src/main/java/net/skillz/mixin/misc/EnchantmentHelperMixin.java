package net.skillz.mixin.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.skillz.access.ItemStackAccess;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Debug(export=true)
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/nbt/NbtList;getCompound(I)Lnet/minecraft/nbt/NbtCompound;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void inject(EnchantmentHelper.Consumer consumer, ItemStack stack, CallbackInfo ci, NbtList nbtList, int i, NbtCompound nbtCompound) {
        if (((ItemStackAccess)(Object)(stack)).skillz$getHoldingPlayer() != null) {
            LevelManager levelManager = ((LevelManagerAccess) ((ItemStackAccess)(Object)(stack)).skillz$getHoldingPlayer()).skillz$getLevelManager();
            Enchantment ench = Registries.ENCHANTMENT.get(EnchantmentHelper.getIdFromNbt(nbtCompound));
            if (!levelManager.hasRequiredEnchantmentLevel(Registries.ENCHANTMENT.getEntry(ench), EnchantmentHelper.getLevelFromNbt(nbtCompound)))
                ci.cancel();
        }
    }

    @Inject(method = "getLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getIdFromNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/util/Identifier;"), cancellable = true)
    private static void inject3(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local NbtCompound nbtCompound) {
        if (((ItemStackAccess)(Object)(stack)).skillz$getHoldingPlayer() != null) {
            LevelManager levelManager = ((LevelManagerAccess) ((ItemStackAccess)(Object)(stack)).skillz$getHoldingPlayer()).skillz$getLevelManager();
            Enchantment ench = Registries.ENCHANTMENT.get(EnchantmentHelper.getEnchantmentId(enchantment));
            if (!levelManager.hasRequiredEnchantmentLevel(Registries.ENCHANTMENT.getEntry(ench), EnchantmentHelper.getLevelFromNbt(nbtCompound))) {
                cir.setReturnValue(0);
            }
        }
    }

    @Inject(method = "onTargetDamaged", at = @At("HEAD"), cancellable = true)
    private static void onTargetDamagedMixin(LivingEntity user, Entity target, CallbackInfo info) {
        if (user instanceof PlayerEntity playerEntity) {
            Item weapon = user.getStackInHand(user.getActiveHand()).getItem();
            if (weapon != null) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
                if (!levelManager.hasRequiredItemLevel(weapon)) {
                    info.cancel();
                }
            }
        }
    }
}
