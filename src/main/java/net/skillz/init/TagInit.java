package net.skillz.init;

import net.minecraft.entity.damage.DamageType;
import net.skillz.SkillZMain;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class TagInit {

    public static final TagKey<Item> FARM_ITEMS = TagKey.of(RegistryKeys.ITEM, SkillZMain.id("farm_items"));
    public static final TagKey<Item> RESTRICTED_FURNACE_EXPERIENCE_ITEMS = TagKey.of(RegistryKeys.ITEM, SkillZMain.id("restricted_furnace_experience_items"));

    public static final TagKey<DamageType> BYPASSES_DAMAGE_EVASION = TagKey.of(RegistryKeys.DAMAGE_TYPE, SkillZMain.id("bypasses_damage_evasion"));

    public static void init() {
    }
}
