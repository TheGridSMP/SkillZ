package net.levelz.mixin.item;

import net.levelz.access.LevelManagerAccess;
import net.levelz.data.LevelLists;
import net.levelz.level.LevelManager;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlockMixin(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (!player.isCreative() && !player.isSpectator()) {
            LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
            if (!levelManager.hasRequiredItemLevel(player.getStackInHand(context.getHand()).getItem())) {
                // player.sendMessage(Text.translatable("item.levelz." + customList.get(customList.indexOf(string) + 1) +
                // ".tooltip", customList.get(customList.indexOf(string) + 2)).formatted(Formatting.RED), true);
                System.out.println("SEX");
                player.sendMessage(Text.translatable("r").formatted(Formatting.RED), true);
                //return TypedActionResult.fail(player.getStackInHand(context.getHand()));
            }
        }
        //return TypedActionResult.pass(ItemStack.EMPTY);
    }
}
