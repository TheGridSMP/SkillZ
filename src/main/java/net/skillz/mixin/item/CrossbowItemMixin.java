package net.skillz.mixin.item;

import net.skillz.util.BonusHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "createArrow", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void createArrowMixin(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> info, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        BonusHelper.crossbowBonus(entity, persistentProjectileEntity);
    }
}
