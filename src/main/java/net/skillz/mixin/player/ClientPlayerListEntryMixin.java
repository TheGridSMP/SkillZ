package net.skillz.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.skillz.access.ClientPlayerListAccess;
import net.minecraft.client.network.PlayerListEntry;

@Environment(EnvType.CLIENT)
@Mixin(PlayerListEntry.class)
public abstract class ClientPlayerListEntryMixin implements ClientPlayerListAccess {

    @Unique
    private int level;

    @Override
    public int skillz$getLevel() {
        return this.level;
    }

    @Override
    public void skillz$setLevel(int level) {
        this.level = level;
    }
}
