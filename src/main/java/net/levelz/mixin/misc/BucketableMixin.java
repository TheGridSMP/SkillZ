package net.levelz.mixin.misc;

import java.util.ArrayList;
import java.util.Optional;

import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

@SuppressWarnings("rawtypes")
@Mixin(Bucketable.class)
public interface BucketableMixin {

    @Inject(method = "Lnet/minecraft/entity/Bucketable;tryBucket(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/entity/LivingEntity;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"), cancellable = true)
    private static <T extends LivingEntity> void tryBucketMixin(PlayerEntity player, Hand hand, T entity, CallbackInfoReturnable<Optional> info) {
        if (player.isCreative()) {
            return;
        }
        LevelManager levelManager = ((LevelManagerAccess) player).getLevelManager();
        if (!levelManager.hasRequiredItemLevel(player.getStackInHand(hand).getItem())) {
            player.sendMessage(Text.translatable("item.levelz.locked.tooltip").formatted(Formatting.RED), true);
            info.setReturnValue(Optional.empty());
        }
    }
}
