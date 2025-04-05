package net.skillz.mixin.item;

import net.skillz.util.BonusHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(BowItem.class)
public class BowItemMixin {

    /*@Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setCurrentHand(Lnet/minecraft/util/Hand;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack) {
        ArrayList<Object> levelList = LevelLists.customItemList;
        String string = Registries.ITEM.getId(itemStack.getItem()).toString();
        if (!levelList.isEmpty() && levelList.contains(string)) {
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                        true);
                info.setReturnValue(TypedActionResult.fail(itemStack));
            }
        } else {
            levelList = LevelLists.bowList;
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
                info.setReturnValue(TypedActionResult.fail(itemStack));
            }
        }
    }*/

    @Inject(method = "onStoppedUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onStoppedUsingMixin(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo info, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity, int j) {
        /*int archeryLevel = ((PlayerStatsManagerAccess) playerEntity).getPlayerStatsManager().getSkillLevel(Skill.ARCHERY);
        persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage()
                + (archeryLevel >= ConfigInit.CONFIG.maxLevel && ConfigInit.CONFIG.archeryDoubleDamageChance > world.random.nextFloat() ? persistentProjectileEntity.getDamage() * 2D
                        : (double) archeryLevel * ConfigInit.CONFIG.archeryBowExtraDamage));*/
        BonusHelper.bowBonus(playerEntity, persistentProjectileEntity);
    }
}