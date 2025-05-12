package net.skillz.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;

public class RegistryHelper {

    public static String enchantmentToString(RegistryEntry<Enchantment> enchantment) {
        return enchantment.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }

    public static String attributeToString(RegistryEntry<EntityAttribute> skillAttribute) {
        return skillAttribute.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }
}
