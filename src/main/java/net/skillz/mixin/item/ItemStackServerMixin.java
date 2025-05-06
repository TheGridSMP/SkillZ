package net.skillz.mixin.item;

import net.skillz.access.ItemStackAccess;
import net.skillz.util.BonusHelper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackServerMixin implements ItemStackAccess {

    @Unique
    private PlayerEntity holdingPlayer = null;

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"))
    private void damageMixin(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (BonusHelper.itemDamageChanceBonus(player))
            cir.cancel();
    }

    @Override
    public PlayerEntity skillz$getHoldingPlayer() {
        return holdingPlayer;
    }

    @Override
    public void skillz$setHoldingPlayer(PlayerEntity player) {
        this.holdingPlayer = player;
    }
}
