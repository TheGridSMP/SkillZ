package net.skillz.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.*;
import net.skillz.network.packet.*;
import net.skillz.registry.EnchantmentRegistry;
import net.skillz.registry.EnchantmentZ;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class PacketHelper {

    public static <T> void writeRegEntry(PacketByteBuf buf, IndexedIterable<RegistryEntry<T>> indexed, RegistryEntry.Reference<T> entry) {
        buf.writeRegistryEntry(indexed, entry, (b, t) -> {
            throw new IllegalStateException("Unreachable.");
        });
    }

    public static <T> RegistryEntry.Reference<T> readRegEntry(PacketByteBuf buf, IndexedIterable<RegistryEntry<T>> indexed) {
        return (RegistryEntry.Reference<T>) buf.readRegistryEntry(indexed, b -> {
            throw new IllegalStateException("Unreachable.");
        });
    }

    public static <T extends Enum<T>> void writeEnum(PacketByteBuf buf, T t) {
        buf.writeEnumConstant(t);
    }

    public static <T extends Enum<T>> T readEnum(PacketByteBuf buf, T[] values) {
        return values[buf.readVarInt()];
    }

    public static <T> void writeCollection(PacketByteBuf buf, Collection<T> collection, BiConsumer<T, PacketByteBuf> writer) {
        buf.writeCollection(collection, (packetByteBuf, t) -> writer.accept(t, packetByteBuf));
    }

    public static <K, V> void writeMap(PacketByteBuf buf, Map<K, V> collection, PacketByteBuf.PacketWriter<K> kWriter, BiConsumer<V, PacketByteBuf> vWriter) {
        buf.writeMap(collection, kWriter, (packetByteBuf, v) -> vWriter.accept(v, packetByteBuf));
    }

    public static <K, V> void writeMap(PacketByteBuf buf, Map<K, V> collection, BiConsumer<K, PacketByteBuf> kWriter, BiConsumer<V, PacketByteBuf> vWriter) {
        buf.writeMap(collection, (packetByteBuf, t) -> kWriter.accept(t, packetByteBuf), (packetByteBuf, v) -> vWriter.accept(v, packetByteBuf));
    }

    public static void updateLevels(ServerPlayerEntity serverPlayerEntity) {
        LevelManager levelManager = ((LevelManagerAccess) serverPlayerEntity).skillz$getLevelManager();
        int overallLevel = levelManager.getOverallLevel();
        Map<Identifier, PlayerPoints> skillPoints = levelManager.getSkillPoints();
        int totalLevelExperience = levelManager.getTotalLevelExperience();
        float levelProgress = levelManager.getLevelProgress();

        ServerPlayNetworking.send(serverPlayerEntity, new LevelPacket(overallLevel, skillPoints, totalLevelExperience, levelProgress));
    }

    public static void updateSkills(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new SkillSyncPacket(LevelManager.SKILLS.values(), LevelManager.BONUSES.values(), LevelManager.POINTS.values()));
    }

    public static void updatePlayerSkills(ServerPlayerEntity player, @Nullable ServerPlayerEntity oldPlayerEntity) {
        LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();
        if (oldPlayerEntity != null) {
            LevelManager oldLevelManager = ((LevelManagerAccess) oldPlayerEntity).skillz$getLevelManager();
            levelManager.setPlayerSkills(oldLevelManager.getPlayerSkills());
            levelManager.setOverallLevel(oldLevelManager.getOverallLevel());
            levelManager.setTotalLevelExperience(oldLevelManager.getTotalLevelExperience());
            levelManager.setSkillPoints(oldLevelManager.getSkillPoints());
            levelManager.setLevelProgress(oldLevelManager.getLevelProgress());
        }

        Collection<PlayerSkill> playerSkills = levelManager.getPlayerSkills().values();
        ServerPlayNetworking.send(player, new PlayerSkillSyncPacket(playerSkills));
    }

    public static void updateRestrictions(ServerPlayerEntity serverPlayerEntity) {
        ServerPlayNetworking.send(serverPlayerEntity, new RestrictionPacket(RestrictionPacket.RestrictionRecord.fromMap(LevelManager.BLOCK_RESTRICTIONS),
                RestrictionPacket.RestrictionRecord.fromMap(LevelManager.CRAFTING_RESTRICTIONS), RestrictionPacket.RestrictionRecord.fromMap(LevelManager.ENTITY_RESTRICTIONS),
                RestrictionPacket.RestrictionRecord.fromMap(LevelManager.ITEM_RESTRICTIONS), RestrictionPacket.RestrictionRecord.fromMap(LevelManager.MINING_RESTRICTIONS),
                RestrictionPacket.RestrictionRecord.fromMap(LevelManager.ENCHANTMENT_RESTRICTIONS)));
    }

    public static void syncEnchantments(ServerPlayerEntity serverPlayerEntity) {
        List<Integer> keys = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<Integer> levels = new ArrayList<>();

        for (Map.Entry<Integer, EnchantmentZ> entry : EnchantmentRegistry.ENCHANTMENTS.entrySet()) {
            keys.add(entry.getKey());
            ids.add(RegistryHelper.enchantmentToString(entry.getValue().getEntry()));
            levels.add(entry.getValue().getLevel());
        }

        ServerPlayNetworking.send(serverPlayerEntity, new EnchantmentZPacket(EnchantmentRegistry.INDEX_ENCHANTMENTS, keys, ids, levels));
    }
}
