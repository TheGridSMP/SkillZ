package net.skillz.mixin.item;

import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(Equipment.class)
public interface EquipmentMixin {

    @Inject(method = "equipAndSwap", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    default void equipAndSwapMixin(Item item, World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info, ItemStack itemStack, EquipmentSlot equipmentSlot, ItemStack itemStack2) {
        /*if (itemStack.getItem() instanceof ElytraItem) {
            ArrayList<Object> levelList = LevelLists.elytraList;
            if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, null, true)) {
                user.sendMessage(Text.translatable("item.levelz." + levelList.get(0) + ".tooltip", levelList.get(1)).formatted(Formatting.RED), true);
                info.setReturnValue(TypedActionResult.fail(itemStack));
            }
        } else if (itemStack.getItem() instanceof ArmorItem) {
            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(itemStack.getItem()).toString())) {
                String string = Registries.ITEM.getId(itemStack.getItem()).toString();
                if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                    user.sendMessage(
                            Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                            true);
                    info.setReturnValue(TypedActionResult.fail(itemStack));
                }
            } else {
                String string = ((ArmorItem) itemStack.getItem()).getMaterial().getName().toLowerCase();
                levelList = LevelLists.armorList;
                if (!PlayerStatsManager.playerLevelisHighEnough(user, levelList, string, true)) {
                    user.sendMessage(
                            Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2)).formatted(Formatting.RED),
                            true);
                    info.setReturnValue(TypedActionResult.fail(itemStack));
                }
            }
        }*/
        if (user.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) user).getLevelManager();
        if (!levelManager.hasRequiredItemLevel(itemStack.getItem())) {
            user.sendMessage(Text.translatable("restriction.skillz.locked.tooltip").formatted(Formatting.RED), true);
            info.setReturnValue(TypedActionResult.fail(itemStack));
        }
    }

}
