package net.skillz.mixin.player;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.skillz.access.ItemStackAccess;
import net.skillz.access.LevelManagerAccess;
import net.skillz.access.PlayerDropAccess;
import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.init.ConfigInit;
import net.skillz.level.LevelManager;
import net.skillz.util.BonusHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export=true)
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LevelManagerAccess, PlayerDropAccess {

    @Unique
    private final LevelManager levelManager = new LevelManager((PlayerEntity) (Object) this);

    @Unique
    private int killedMobsInChunk;

    @Unique
    @Nullable
    private Chunk killedMobChunk;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.levelManager.readNbt(nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.levelManager.writeNbt(nbt);
    }

    @ModifyArg(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"), index = 0)
    private float injected(float original) {
        original *= BonusHelper.exhaustionReductionBonus((PlayerEntity) (Object) this);
        return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private boolean attackKnockbackChanceMixin(boolean original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return original || BonusHelper.meleeKnockbackAttackChanceBonus(player);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 1), ordinal = 2)
    private boolean attackCriticalChanceMixin(boolean original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return original || BonusHelper.meleeCriticalAttackChance(player);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private float attackMixin(float original) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.isCreative() || levelManager.hasRequiredItemLevel(this.getMainHandStack().getItem()))
            return original;

        return 0;
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    private float attackCriticalDamageMixin(float original) {
        original /= 1.5F;
        original += BonusHelper.meleeCriticalDamageBonus((PlayerEntity) (Object) this);
        original *= 1.5F;
        return original;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    private float attackDoubleDamageMixin(float original) {
        if (BonusHelper.meleeDoubleDamageBonus((PlayerEntity) (Object) this))
            original *= 2f;

        return original;
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        BonusHelper.damageReflectionBonus(player, source, amount);

        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && BonusHelper.evadingDamageBonus(player))
            info.setReturnValue(false);
    }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    private void eatFoodMixin(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
        BonusHelper.foodIncreasionBonus((PlayerEntity) (Object) this, stack);
    }

    @Override
    public LevelManager skillz$getLevelManager() {
        return this.levelManager;
    }

    @Override
    public void skillz$mobKilled(Chunk chunk) {
        if (killedMobChunk != null && killedMobChunk == chunk) {
            killedMobsInChunk++;
        } else {
            killedMobChunk = chunk;
            killedMobsInChunk = 0;
        }
    }

    @Override
    public void skillz$resetMobKills() {
        killedMobsInChunk = 0;
    }

    @Override
    public boolean skillz$allowMobDrop() {
        return killedMobsInChunk < ConfigInit.MAIN.LEVEL.mobKillCount;
    }

    @Override
    protected void dropXp() {
        if (this.getWorld() instanceof ServerWorld serverWorld && this.shouldDropXp() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT) && ConfigInit.MAIN.EXPERIENCE.resetCurrentXp)
            LevelExperienceOrbEntity.spawn(serverWorld, this.getPos(), (int) (this.levelManager.getLevelProgress() * this.levelManager.getNextLevelExperience()));

        super.dropXp();
    }

    @ModifyReturnValue(method = "getEquippedStack", at = @At(value = "RETURN"))
    private ItemStack setHolder(ItemStack original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ((ItemStackAccess) (Object) original).skillz$setHoldingPlayer(player);
        return original;
    }
}