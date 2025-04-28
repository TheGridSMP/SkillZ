package net.skillz.mixin.player;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.skillz.access.PlayerBreakBlockAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Debug(export = true)
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements PlayerBreakBlockAccess {

    /*@Shadow
    @Mutable
    @Final
    public PlayerEntity player;

    @Shadow
    @Mutable
    @Final
    public DefaultedList<ItemStack> main;*/

    @Shadow
    public int selectedSlot;

    /*public boolean canBreakBlock = true;

    public float blockBreakExtraDelta = 1.0F;

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "HEAD"), cancellable = true)
    private void getBlockBreakingSpeedMixin(BlockState block, CallbackInfoReturnable<Float> info) {
        if (!this.canBreakBlock)
            info.setReturnValue(1.0F);
    }*/

    @Inject(method = "updateItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;inventoryTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V"))
    private void test(CallbackInfo ci, @Local DefaultedList<ItemStack> defaultedList, @Local int i) {
        /*ItemStack item = defaultedList.get(i);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            item.getAttributeModifiers(slot).forEach((k, v )-> {

            });
        }
        System.out.println(selectedSlot);*/
    }

    /*@Override
    public void setInventoryBlockBreakable(boolean breakable) {
        this.canBreakBlock = breakable;
    }

    @Override
    public void setAbstractBlockBreakDelta(float breakingDelta) {
        this.blockBreakExtraDelta = breakingDelta;
    }

    @Override
    public float getBreakingAbstractBlockDelta() {
        return this.blockBreakExtraDelta;
    }*/

}
