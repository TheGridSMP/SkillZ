package net.skillz.mixin.item;

import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(SwordItem.class)
public class SwordItemMixin {

    /*@Inject(method = "postHit", at = @At("HEAD"), cancellable = true)
    private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof PlayerEntity playerEntity) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(stack.getItem()).toString(), true))
                    info.setReturnValue(false);
            } else {
                levelList = LevelLists.swordList;
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((SwordItem) stack.getItem()).getMaterial().toString().toLowerCase(), true))
                    info.setReturnValue(false);
            }
        }

    }

    @Inject(method = "postMine", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner, CallbackInfoReturnable<Boolean> info) {
        if (miner instanceof PlayerEntity playerEntity) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, Registries.ITEM.getId(stack.getItem()).toString(), true))
                    info.setReturnValue(false);
            } else {
                levelList = LevelLists.swordList;
                if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, ((SwordItem) stack.getItem()).getMaterial().toString().toLowerCase(), true))
                    info.setReturnValue(false);
            }
        }
    }*/

    @Inject(method = "postHit", at = @At("HEAD"), cancellable = true)
    private void postHitMixin(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
            if (!levelManager.hasRequiredItemLevel(stack.getItem())) {
                info.setReturnValue(false);
            }
        }

    }

    @Inject(method = "postMine", at = @At("HEAD"), cancellable = true)
    private void postMineMixin(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity attacker, CallbackInfoReturnable<Boolean> info) {
        if (attacker instanceof PlayerEntity playerEntity) {
            if (playerEntity.isCreative()) {
                return;
            }
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
            if (!levelManager.hasRequiredItemLevel(stack.getItem())) {
                info.setReturnValue(false);
            }
        }
    }

}
