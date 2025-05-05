package net.skillz.mixin.network;

import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.skillz.network.packet.OrbPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(NetworkState.class)
public class NetworkStateMixin {

    @ModifyArg(method = "<clinit>", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;setup(Lnet/minecraft/network/NetworkSide;Lnet/minecraft/network/NetworkState$PacketHandler;)Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;HANDSHAKING:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER), to = @At(value = "FIELD", target = "Lnet/minecraft/network/NetworkState;PLAY:Lnet/minecraft/network/NetworkState;", opcode = Opcodes.PUTSTATIC)))
    private static NetworkState.PacketHandler<ClientPlayPacketListener> setupClientbound(NetworkState.PacketHandler<ClientPlayPacketListener> handler) {
        return handler.register(OrbPacket.class, OrbPacket::new);
    }

}
