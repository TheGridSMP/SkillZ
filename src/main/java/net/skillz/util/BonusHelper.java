package net.skillz.util;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.entity.passive.PassiveEntity;
import net.skillz.access.LevelManagerAccess;
import net.skillz.entity.LevelExperienceOrbEntity;
import net.skillz.init.ConfigInit;
import net.skillz.init.TagInit;
import net.skillz.level.LevelManager;
import net.skillz.level.SkillBonus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// TODO: replace with attributes
public class BonusHelper {

    public interface R {
        void run(int level);
    }

    public interface F<T> {
        T apply(int level);
    }

    private static boolean doChanceCheck(String bonnusKey, PlayerEntity playerEntity, float bonusChance) {
        return doAcceptBonus(bonnusKey, playerEntity, level -> playerEntity.getRandom().nextFloat() <= level * bonusChance, false);
    }

    private static boolean doRunnableBonus(String bonusKey, PlayerEntity playerEntity, R runner) {
        return doAcceptBonus(bonusKey, playerEntity, level -> {
            runner.run(level);
            return true;
        }, false);
    }

    private static <T> T doAcceptBonus(String bonusKey, PlayerEntity player, F<T> f, T def) {
        if (LevelManager.BONUSES.containsKey(bonusKey)) {
            LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get(bonusKey);
            int level = levelManager.getPlayerSkills().get(skillBonus.skillId()).getLevel();

            if (level >= skillBonus.level())
                return f.apply(level);
        }

        return def;
    }

    public static void bowBonus(PlayerEntity playerEntity, PersistentProjectileEntity persistentProjectileEntity) {
        BonusHelper.doRunnableBonus("bowDamage", playerEntity,
                level -> persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + ConfigInit.MAIN.BONUSES.bowDamageBonus * level));

        BonusHelper.doRunnableBonus("bowDoubleDamageChance", playerEntity,
                level -> persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * 2D));
    }

    public static void crossbowBonus(LivingEntity entity, PersistentProjectileEntity persistentProjectileEntity) {
        if (entity instanceof PlayerEntity playerEntity) {
            BonusHelper.doRunnableBonus("crossbowDamage", playerEntity, level ->
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + ConfigInit.MAIN.BONUSES.crossbowDamageBonus * level));

            BonusHelper.doRunnableBonus("crossbowDoubleDamageChance", playerEntity, level -> {
                if (playerEntity.getRandom().nextFloat() <= ConfigInit.MAIN.BONUSES.crossbowDoubleDamageChanceBonus)
                    persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() * 2D);
            });
        }
    }

    public static boolean itemDamageChanceBonus(@Nullable PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("itemDamageChance", playerEntity, ConfigInit.MAIN.BONUSES.itemDamageChanceBonus);
    }

    public static List<StatusEffectInstance> potionEffectChanceBonus(List<StatusEffectInstance> original, LivingEntity user) {
        if (!(user instanceof PlayerEntity playerEntity))
            return original;

        return BonusHelper.doAcceptBonus("potionEffectChance", playerEntity, level -> {
            if (playerEntity.getRandom().nextFloat() > ConfigInit.MAIN.BONUSES.potionEffectChanceBonus)
                return original;

            List<StatusEffectInstance> newEffectList = new ArrayList<>();
            original.forEach(effect -> {
                newEffectList.add(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()));
            });

            return newEffectList;
        }, original);
    }

    public static void breedTwinChanceBonus(ServerWorld world, PlayerEntity playerEntity, PassiveEntity other, PassiveEntity passiveEntity) {
        BonusHelper.doRunnableBonus("breedTwinChance", playerEntity, (level) -> {
            if (playerEntity.getRandom().nextFloat() <= ConfigInit.MAIN.BONUSES.twinBreedChanceBonus) {
                PassiveEntity extraPassiveEntity = passiveEntity.createChild(world, other);
                extraPassiveEntity.setBaby(true);
                extraPassiveEntity.refreshPositionAndAngles(passiveEntity.getX(), passiveEntity.getY(), passiveEntity.getZ(), playerEntity.getRandom().nextFloat() * 360F, 0.0F);
                world.spawnEntityAndPassengers(extraPassiveEntity);
            }
        });
    }

    public static float fallDamageReductionBonus(PlayerEntity playerEntity) {
        return BonusHelper.doAcceptBonus("fallDamageReduction", playerEntity, level -> level * ConfigInit.MAIN.BONUSES.fallDamageReductionBonus, 0f);
    }

    public static boolean deathGraceChanceBonus(PlayerEntity playerEntity) {
        return BonusHelper.doRunnableBonus("deathGraceChance", playerEntity, level -> {
            playerEntity.setHealth(1.0F);
            playerEntity.clearStatusEffects();
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 600, 0));
        });
    }

    public static float tntStrengthBonus(PlayerEntity playerEntity) {
        return BonusHelper.doAcceptBonus("tntStrength", playerEntity, level -> ConfigInit.MAIN.BONUSES.tntStrengthBonus * level, 0f);
    }

    public static float priceDiscountBonus(PlayerEntity playerEntity) {
        if (playerEntity.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE))
            return 1.0f;

        return BonusHelper.doAcceptBonus("priceDiscount", playerEntity, level -> 1.0f - level * ConfigInit.MAIN.BONUSES.priceDiscountBonus, 1f);
    }

    public static void tradeXpBonus(ServerWorld serverWorld, @Nullable PlayerEntity playerEntity, MerchantEntity merchantEntity, int amount) {
        amount = (int) (amount * ConfigInit.MAIN.EXPERIENCE.tradingXPMultiplier);
        if (amount > 0) {
            if (playerEntity != null) {
                if (LevelManager.BONUSES.containsKey("tradeXp")) {
                    LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
                    SkillBonus skillBonus = LevelManager.BONUSES.get("tradeXp");
                    int level = levelManager.getPlayerSkills().get(skillBonus.skillId()).getLevel();
                    if (level >= skillBonus.level()) {
                        amount = (int) (amount * level * ConfigInit.MAIN.BONUSES.tradeXpBonus);
                    }
                }
            }
            LevelExperienceOrbEntity.spawn(serverWorld, merchantEntity.getPos().add(0.0D, 0.5D, 0.0D), amount);
            // Todo: HERE
            // ? 1.0F + ConfigInit.CONFIG.basedOnMultiplier * ((PlayerStatsManagerAccess) lastCustomer).getPlayerStatsManager().getOverallLevel()
        }
    }

    public static boolean merchantImmuneBonus(PlayerEntity playerEntity) {
        return BonusHelper.doAcceptBonus("merchantImmune", playerEntity, integer -> true, false);
    }

    public static void miningDropChanceBonus(PlayerEntity playerEntity, BlockState state, BlockPos pos, LootContextParameterSet.Builder builder) {
        if (state.isIn(ConventionalBlockTags.ORES) && EnchantmentHelper.getEquipmentLevel(Enchantments.SILK_TOUCH, playerEntity) <= 0) {
            if (LevelManager.BONUSES.containsKey("miningDropChance")) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
                SkillBonus skillBonus = LevelManager.BONUSES.get("miningDropChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.skillId()).getLevel();
                if (level >= skillBonus.level() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.MAIN.BONUSES.miningDropChanceBonus) {
                    List<ItemStack> list = state.getDroppedStacks(builder);
                    if (!list.isEmpty()) {
                        Block.dropStack(playerEntity.getWorld(), pos, state.getDroppedStacks(builder).get(0).split(1));
                    }
                }
            }
        }
    }

    public static void plantDropChanceBonus(PlayerEntity playerEntity, BlockState state, BlockPos pos) {
        if (EnchantmentHelper.getEquipmentLevel(Enchantments.SILK_TOUCH, playerEntity) <= 0) {
            if (LevelManager.BONUSES.containsKey("plantDropChance")) {
                LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
                SkillBonus skillBonus = LevelManager.BONUSES.get("plantDropChance");
                int level = levelManager.getPlayerSkills().get(skillBonus.skillId()).getLevel();
                if (level >= skillBonus.level() && playerEntity.getRandom().nextFloat() <= level * ConfigInit.MAIN.BONUSES.plantDropChanceBonus) {
                    List<ItemStack> list = Block.getDroppedStacks(state, (ServerWorld) playerEntity.getWorld(), pos, null);
                    for (ItemStack itemStack : list) {
                        if (itemStack.isIn(TagInit.FARM_ITEMS)) {
                            Block.dropStack(playerEntity.getWorld(), pos, itemStack);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static boolean anvilXpCapBonus(PlayerEntity playerEntity) {
        return BonusHelper.doRunnableBonus("anvilXpCap", playerEntity, level -> {});
    }

    public static int anvilXpDiscountBonus(PlayerEntity playerEntity, int levelCost) {
        if (levelCost > ConfigInit.MAIN.BONUSES.anvilXpCap && anvilXpCapBonus(playerEntity))
            return ConfigInit.MAIN.BONUSES.anvilXpCap;

        return BonusHelper.doAcceptBonus("anvilXpDiscount", playerEntity, level ->
                (int) (levelCost * (1.0f - level * ConfigInit.MAIN.BONUSES.anvilXpDiscountBonus)), levelCost);
    }

    public static boolean anvilXpChanceBonus(PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("anvilXpChance", playerEntity, ConfigInit.MAIN.BONUSES.anvilXpChanceBonus);
    }

    public static void healthAbsorptionBonus(PlayerEntity playerEntity) {
        BonusHelper.doRunnableBonus("healthAbsorption", playerEntity, level ->
                playerEntity.setAbsorptionAmount(ConfigInit.MAIN.BONUSES.healthAbsorptionBonus));
    }

    public static void heatlhRegenBonus(PlayerEntity player) {
        BonusHelper.doRunnableBonus("healthRegen", player, level ->
                player.heal(level * ConfigInit.MAIN.BONUSES.healthRegenBonus));
    }

    public static float exhaustionReductionBonus(PlayerEntity playerEntity) {
        return BonusHelper.doAcceptBonus("exhaustionReduction", playerEntity, level ->
                1.0f - (level * ConfigInit.MAIN.BONUSES.exhaustionReductionBonus),0f);
    }

    public static boolean meleeKnockbackAttackChanceBonus(PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("meleeKnockbackAttackChance", playerEntity, ConfigInit.MAIN.BONUSES.meleeKnockbackAttackChanceBonus);
    }

    public static boolean meleeCriticalAttackChance(PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("meleeCriticalAttackChance", playerEntity, ConfigInit.MAIN.BONUSES.meleeCriticalAttackChanceBonus);
    }

    public static float meleeCriticalDamageBonus(PlayerEntity playerEntity) {
        return BonusHelper.doAcceptBonus("meleeCriticalAttackDamage", playerEntity, level ->
                level * ConfigInit.MAIN.BONUSES.meleeCriticalAttackDamageBonus, 0f);
    }

    public static boolean meleeDoubleDamageBonus(PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("meleeDoubleAttackDamageChance", playerEntity, ConfigInit.MAIN.BONUSES.meleeDoubleAttackDamageChanceBonus);
    }

    public static void foodIncreasionBonus(PlayerEntity playerEntity, ItemStack itemStack) {
        if (LevelManager.BONUSES.containsKey("foodIncreasion") && itemStack.getItem().isFood()) {
            LevelManager levelManager = ((LevelManagerAccess) playerEntity).skillz$getLevelManager();
            SkillBonus skillBonus = LevelManager.BONUSES.get("foodIncreasion");
            int level = levelManager.getPlayerSkills().get(skillBonus.skillId()).getLevel();
            if (level >= skillBonus.level()) {
                net.minecraft.item.FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
                float multiplier = level * ConfigInit.MAIN.BONUSES.foodIncreasionBonus;
                playerEntity.getHungerManager().add((int) (foodComponent.getHunger() * multiplier), foodComponent.getSaturationModifier() * multiplier);
            }
        }
    }

    public static void damageReflectionBonus(PlayerEntity playerEntity, DamageSource source, float amount) {
        if (source.getAttacker() == null)
            return;

        if (!BonusHelper.doChanceCheck("damageReflectionChance", playerEntity, ConfigInit.MAIN.BONUSES.damageReflectionChanceBonus))
            return;

        BonusHelper.doRunnableBonus("damageReflection", playerEntity, level ->
                source.getAttacker().damage(source, amount * level * ConfigInit.MAIN.BONUSES.damageReflectionBonus));
    }

    public static boolean evadingDamageBonus(PlayerEntity playerEntity) {
        return BonusHelper.doChanceCheck("evadingDamageChance", playerEntity, ConfigInit.MAIN.BONUSES.evadingDamageChanceBonus);
    }
}
