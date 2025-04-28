package net.skillz.mixin.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.skillz.SkillZMain;
import net.skillz.access.LevelManagerAccess;
import net.skillz.level.LevelManager;
import net.skillz.util.LevelHelper;
import net.skillz.util.PacketHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(Item.class)
public class ItemMixin {

    @Unique
    public ArrayList<UUID> used = new ArrayList<>();

    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    private void hold(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity) {
            stack.getAttributeModifiers(EquipmentSlot.MAINHAND).forEach((k, v )-> {
                if (k == SkillZMain.skillAttribute) {
                    LevelManager levelManager = ((LevelManagerAccess) entity).getLevelManager();
                    if (selected) {
                        //((ServerPlayerEntity) entity).getAttributeValue(SkillZMain.skillAttribute);
                        if (!used.contains(v.getId())) {
                            final int level = levelManager.getSkillLevel(v.getName());
                            levelManager.setSkillLevel(v.getName(), level + 2);
                            PacketHelper.updatePlayerSkills((ServerPlayerEntity) entity, null);
                            LevelHelper.updateSkill((ServerPlayerEntity) entity, LevelManager.SKILLS.get(v.getName()));
                            used.add(v.getId());
                        }
                    } else {
                        for (UUID uuid : used) {
                            if (uuid.equals(v.getId())) {
                                final int level = levelManager.getSkillLevel(v.getName());
                                levelManager.setSkillLevel(v.getName(), level - 2);
                                PacketHelper.updatePlayerSkills((ServerPlayerEntity) entity, null);
                                LevelHelper.updateSkill((ServerPlayerEntity) entity, LevelManager.SKILLS.get(v.getName()));
                            }
                        }
                        used.removeIf(uuid ->
                            uuid.equals(v.getId())
                        );
                    }
                }
            });
        }
    }
}
