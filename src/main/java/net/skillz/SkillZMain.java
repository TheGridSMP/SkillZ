package net.skillz;

import net.fabricmc.api.ModInitializer;
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

    @Override
    public void onInitialize() {
        CommandInit.init();
        CompatInit.init();
        ConfigInit.init();
        CriteriaInit.init();
        EntityInit.init();
        EventInit.init();
        LoaderInit.init();
        //JsonReaderInit.init();
        //PlayerStatsServerPacket.init();
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
}

// vvv this is bars vvv
// You are LOVED!!!
// Jesus loves you unconditionally!
