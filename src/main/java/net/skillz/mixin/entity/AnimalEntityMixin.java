package net.skillz.mixin.entity;

import net.skillz.access.LevelManagerAccess;
import net.skillz.util.BonusHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {

    @Shadow
    @Nullable
    public abstract ServerPlayerEntity getLovingPlayer();

    public AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void breedSummon(ServerWorld world, AnimalEntity other, CallbackInfo info, PassiveEntity passiveEntity) {
        PlayerEntity playerEntity = getLovingPlayer() != null ? getLovingPlayer() : other.getLovingPlayer();

        if (playerEntity == null)
            return;

        BonusHelper.breedTwinChanceBonus(world, playerEntity, other, passiveEntity);
    }

    @Inject(method = "breed(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/AnimalEntity;Lnet/minecraft/entity/passive/PassiveEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private void breedXp(ServerWorld world, AnimalEntity other, @Nullable PassiveEntity baby, CallbackInfo info) {
        if (ConfigInit.MAIN.EXPERIENCE.breedingXPMultiplier > 0.0F) {
            LevelExperienceOrbEntity.spawn(world, this.getPos().add(0.0D, 0.1D, 0.0D),
                    (int) ((this.getRandom().nextInt(7) + 1) * ConfigInit.MAIN.EXPERIENCE.breedingXPMultiplier
                            * (ConfigInit.MAIN.EXPERIENCE.dropXPbasedOnLvl && getLovingPlayer() != null
                            ? 1.0F + ConfigInit.MAIN.EXPERIENCE.basedOnMultiplier * ((LevelManagerAccess) getLovingPlayer()).skillz$getLevelManager().getOverallLevel()
                            : 1.0F)));
        }
    }
}
