package net.skillz.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import net.skillz.util.BonusHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.skillz.access.MobEntityAccess;
import net.skillz.access.PlayerDropAccess;
import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.init.ConfigInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Debug(export=true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected int playerHitTimer;

    @Shadow
    @Nullable
    protected PlayerEntity attackingPlayer;

    @Shadow
    public abstract int getXpToDrop();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "modifyAppliedDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I"), ordinal = 0)
    private int modifyAppliedDamageMixin(int original, DamageSource source, float amount) {
        return 1;
    }

    @ModifyArg(method = "modifyAppliedDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DamageUtil;getInflictedDamage(FF)F"), index = 1)
    private float modifyAppliedDamageMixin2(float damageDealt, @Local DamageSource source) {
        float damage = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), source);

        if (source.isOf(DamageTypes.FALL) && (Object) this instanceof PlayerEntity playerEntity)
            damage += BonusHelper.fallDamageReductionBonus(playerEntity);

        return damage;
    }

    @ModifyVariable(method = "tryUseTotem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack tryUseTotemMixin(ItemStack original) {
        if ((Object) this instanceof PlayerEntity playerEntity && original.isOf(Items.TOTEM_OF_UNDYING)) {
            if (playerEntity.isCreative())
                return original;

            LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();

            if (!levelManager.hasRequiredItemLevel(original.getItem()))
                return ItemStack.EMPTY;
        }

        return original;
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"), cancellable = true)
    private void tryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        if ((Object) this instanceof PlayerEntity player && BonusHelper.deathGraceChanceBonus(player)) {
            player.setHealth(1.0F);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            info.setReturnValue(true);
        }
    }

    @Inject(method = "drop", at = @At(value = "HEAD"), cancellable = true)
    protected void dropMixin(DamageSource source, CallbackInfo info) {
        if (!((Object) this instanceof PlayerEntity) && attackingPlayer != null && this.playerHitTimer > 0 && ConfigInit.MAIN.LEVEL.disableMobFarms
                && !((PlayerDropAccess) attackingPlayer).skillz$allowMobDrop()) {
            info.cancel();
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        if (attackingPlayer != null && this.playerHitTimer > 0 && ConfigInit.MAIN.LEVEL.disableMobFarms) {
            ((PlayerDropAccess) attackingPlayer).skillz$mobKilled(this.getWorld().getChunk(this.getBlockPos()));
        }
    }

    @Inject(method = "dropXp", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ExperienceOrbEntity;spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/Vec3d;I)V"))
    protected void dropXpMixin(CallbackInfo info) {
        if (ConfigInit.MAIN.EXPERIENCE.mobXPMultiplier > 0.0F) {
            if (ConfigInit.MAIN.EXPERIENCE.spawnerMobXP || !((Object) this instanceof MobEntityAccess mobEntity) || !mobEntity.skillz$isSpawnerMob()) {
                LevelExperienceOrbEntity.spawn((ServerWorld) this.getWorld(), this.getPos(),
                        (int) (this.getXpToDrop() * ConfigInit.MAIN.EXPERIENCE.mobXPMultiplier
                                * (ConfigInit.MAIN.EXPERIENCE.dropXPbasedOnLvl && this.attackingPlayer != null
                                        ? 1.0F + ConfigInit.MAIN.EXPERIENCE.basedOnMultiplier * ((LevelManagerAccess) this.attackingPlayer).skillz$getLevelManager().getOverallLevel()
                                        : 1.0F)));
            }
        }
    }
}