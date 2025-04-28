package net.skillz;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.skillz.init.*;
import net.skillz.network.LevelServerPacket;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SkillZMain implements ModInitializer {
    public static final String MOD_ID = "skillz";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /*public static EntityAttribute skillAttribute = register(
            "item_bonus", new ClampedEntityAttribute("attribute.name.item_bonus", 0.0, -9999.0, 9999.0).setTracked(true)
    );*/

    @Override
    public void onInitialize() {
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        CriteriaInit.init();
        EntityInit.init();
        EventInit.init();
        LoaderInit.init();
        LevelServerPacket.init();
        TagInit.init();
        ItemInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of(MOD_ID, name);
    }

    public static String getEnchantmentIdAsString(RegistryEntry<Enchantment> enchantment) {
        return (String)enchantment.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }

    public static String getEntityAttributeIdAsString(RegistryEntry<EntityAttribute> skillAttribute) {
        return (String)skillAttribute.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
    }

    /*private static EntityAttribute register(String id, EntityAttribute attribute) {
        return Registry.register(Registries.ATTRIBUTE, id, attribute);
    }*/
}

// vvv this is bars vvv
// You are LOVED!!!
// Jesus loves you unconditionally!
