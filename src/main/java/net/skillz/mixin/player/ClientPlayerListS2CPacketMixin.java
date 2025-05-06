package net.skillz.mixin.player;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.skillz.access.LevelManagerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.skillz.access.ClientPlayerListAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerListS2CPacket.class)
public abstract class ClientPlayerListS2CPacketMixin implements ClientPlayerListAccess {

    @Unique
    private Map<UUID, Integer> levelMap = new HashMap<>();

    @Inject(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(EnumSet<PlayerListS2CPacket.Action> actions, Collection<ServerPlayerEntity> players, CallbackInfo info) {
        players.forEach((player) -> this.levelMap.put(player.getUuid(), ((LevelManagerAccess) player).skillz$getLevelManager().getOverallLevel()));
    }

    @Inject(method = "<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(PlayerListS2CPacket.Action action, ServerPlayerEntity player, CallbackInfo info) {
        this.levelMap.put(player.getUuid(), ((LevelManagerAccess) player).skillz$getLevelManager().getOverallLevel());
    }

    @Inject(method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V", at = @At("TAIL"))
    public void playerListS2CPacketMixin(PacketByteBuf buf, CallbackInfo info) {
        this.levelMap = buf.readMap(PacketByteBuf::readUuid, PacketByteBuf::readInt);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void writeMixin(PacketByteBuf buf, CallbackInfo info) {
        buf.writeMap(this.levelMap, PacketByteBuf::writeUuid, PacketByteBuf::writeInt);
    }

    @Override
    public Map<UUID, Integer> skillz$getLevelMap() {
        return this.levelMap;
    }
}
