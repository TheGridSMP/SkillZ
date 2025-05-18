package net.skillz.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;

public class RegistryHelper {

    public static String enchantmentToString(RegistryEntry<Enchantment> enchantment) {
        return enchantment.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }
}
