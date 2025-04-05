package net.skillz.mixin.network;

import net.skillz.access.OrbAccess;
import net.skillz.network.packet.OrbPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerMixin extends OrbAccess {

    @Override
    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
