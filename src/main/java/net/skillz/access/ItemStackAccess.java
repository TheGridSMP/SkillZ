package net.skillz.access;

import net.minecraft.entity.player.PlayerEntity;

public interface ItemStackAccess {

    PlayerEntity getHoldingPlayer();

    void setHoldingPlayer(PlayerEntity player);

}
