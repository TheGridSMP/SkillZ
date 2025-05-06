package net.skillz.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.skillz.access.MobEntityAccess;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements MobEntityAccess {

    @Unique
    private boolean spawnerMob = false;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.spawnerMob = nbt.getBoolean("SpawnerMob");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("SpawnerMob", this.spawnerMob);
    }

    @Override
    public void skillz$setSpawnerMob(boolean spawnerMob) {
        this.spawnerMob = spawnerMob;
    }

    @Override
    public boolean skillz$isSpawnerMob() {
        return this.spawnerMob;
    }
}
