package net.skillz.level;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.IndexedIterable;
import net.skillz.util.PacketHelper;

public record SkillAttribute(boolean hidden, RegistryEntry.Reference<EntityAttribute> attribute, float baseValue,
                             float levelValue, EntityAttributeModifier.Operation operation) {

    // some caching :P
    static final IndexedIterable<RegistryEntry<EntityAttribute>> INDEXED_ATTRIBUTES = Registries.ATTRIBUTE.getIndexedEntries();
    static final EntityAttributeModifier.Operation[] OPERATIONS = EntityAttributeModifier.Operation.values();

    public static SkillAttribute fromBuf(PacketByteBuf buf) {
        boolean hidden = buf.readBoolean();
        RegistryEntry.Reference<EntityAttribute> attribute = PacketHelper.readRegEntry(buf, INDEXED_ATTRIBUTES);

        float baseValue = buf.readFloat();
        float levelValue = buf.readFloat();

        EntityAttributeModifier.Operation operation = PacketHelper.readEnum(buf, OPERATIONS);
        return new SkillAttribute(hidden, attribute, baseValue, levelValue, operation);
    }

    public void writeBuf(PacketByteBuf buf) {
        buf.writeBoolean(this.hidden);
        PacketHelper.writeRegEntry(buf, INDEXED_ATTRIBUTES, this.attribute);

        buf.writeFloat(this.baseValue);
        buf.writeFloat(this.levelValue);

        PacketHelper.writeEnum(buf, this.operation);
    }
}
