package net.skillz.access;

import net.minecraft.entity.player.PlayerEntity;

public interface ItemStackAccess {

    PlayerEntity skillz$getHoldingPlayer();

    void skillz$setHoldingPlayer(PlayerEntity player);
}
