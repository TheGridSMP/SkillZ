package net.skillz.mixin.entity;

import net.skillz.access.LevelManagerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.init.ConfigInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleEntityMixin extends ThrownItemEntity {

    public ExperienceBottleEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/thrown/ExperienceBottleEntity;discard()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void onCollisionMixin(HitResult hitResult, CallbackInfo info, int i) {
        if (ConfigInit.MAIN.EXPERIENCE.bottleXPMultiplier > 0.0F)
            LevelExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos().add(0.0D, 0.5D, 0.0D),
                    (int) (i * ConfigInit.MAIN.EXPERIENCE.bottleXPMultiplier
                            * (ConfigInit.MAIN.EXPERIENCE.dropXPbasedOnLvl && this.getOwner() != null && this.getOwner() instanceof ServerPlayerEntity serverPlayerEntity
                                    ? 1.0F + ConfigInit.MAIN.EXPERIENCE.basedOnMultiplier * ((LevelManagerAccess) serverPlayerEntity).skillz$getLevelManager().getOverallLevel()
                                    : 1.0F)));
    }
}
