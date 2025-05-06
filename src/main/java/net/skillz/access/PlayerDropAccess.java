package net.skillz.access;

import net.minecraft.world.chunk.Chunk;

public interface PlayerDropAccess {

    void skillz$mobKilled(Chunk chunk);

    boolean skillz$allowMobDrop();

    void skillz$resetMobKills();
}
