package net.skillz.mixin.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;

import net.skillz.access.PlayerDropAccess;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    /*@Inject(method = "checkLootInteraction", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContextParameterSet$Builder;luck(F)Lnet/minecraft/loot/context/LootContextParameterSet$Builder;"))
    private void checkLootInteractionMixin(@Nullable PlayerEntity player, CallbackInfo info) {
        ((PlayerDropAccess) player).resetKilledMobStat();
    }*/

    @Inject(method = "checkUnlocked", at = @At("RETURN"))
    private void checkUnlockedMixin(PlayerEntity player, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            ((PlayerDropAccess) player).skillz$resetMobKills();
        }
    }
}
