package net.levelz.access;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface EnchantmentAccess {

    void forEachEnchantment(LivingEntity entity, EnchantmentHelper.Consumer consumer, ItemStack stack);
    float getAttackDamage(LivingEntity entity, ItemStack stack, EntityGroup group);
}
