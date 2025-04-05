package net.skillz.access;

import net.skillz.network.packet.OrbPacket;

public interface OrbAccess {
    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
