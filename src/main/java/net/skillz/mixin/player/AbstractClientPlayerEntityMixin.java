package net.skillz.mixin.player;

import net.skillz.access.ClientPlayerAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.skillz.access.ClientPlayerListAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin implements ClientPlayerListAccess, ClientPlayerAccess {

    @Unique
    private boolean shouldRenderClientName = true;

    @Shadow
    @Nullable
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public int skillz$getLevel() {
        if (getPlayerListEntry() != null) {
            return ((ClientPlayerListAccess) getPlayerListEntry()).skillz$getLevel();
        }
        return 0;
    }

    @Override
    public boolean skillz$shouldRenderClientName() {
        return this.shouldRenderClientName;
    }

    @Override
    public void skillz$setShouldRenderClientName(boolean shouldRenderClientName) {
        this.shouldRenderClientName = shouldRenderClientName;
    }
}
