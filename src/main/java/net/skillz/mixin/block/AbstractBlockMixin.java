package net.skillz.mixin.block;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractBlock;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    /*@ModifyVariable(method = "calcBlockBreakingDelta", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getBlockBreakingSpeed(Lnet/minecraft/block/BlockState;)F"), ordinal = 0)
    private int calcBlockBreakingDeltaMixin(int original, BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        return (int) (original * ((PlayerBreakBlockAccess) player.getInventory()).getBreakingAbstractBlockDelta());
    }*/
}