package net.skillz.level;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

public class SkillAttribute {

    private final boolean hidden;
    private final RegistryEntry<EntityAttribute> attribute;
    private final float baseValue;
    private final float levelValue;
    private final EntityAttributeModifier.Operation operation;

    public SkillAttribute(boolean hidden, RegistryEntry<EntityAttribute> attribute, float baseValue, float levelValue, EntityAttributeModifier.Operation operation) {
        this.hidden = hidden;
        this.attribute = attribute;
        this.baseValue = baseValue;
        this.levelValue = levelValue;
        this.operation = operation;
    }

    public boolean isHidden() {
        return hidden;
    }

    public RegistryEntry<EntityAttribute> getAttribute() {
        return attribute;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public float getLevelValue() {
        return levelValue;
    }

    public EntityAttributeModifier.Operation getOperation() {
        return operation;
    }
}
