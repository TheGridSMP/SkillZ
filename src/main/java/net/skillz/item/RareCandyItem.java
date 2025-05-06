package net.skillz.item;

import net.skillz.access.LevelManagerAccess;
import net.skillz.access.ServerPlayerSyncAccess;
import net.skillz.level.LevelManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

// Texture made by Pois1x
public class RareCandyItem extends Item {

    public RareCandyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient()) {
            if (!user.isCreative()) {
                stack.decrement(1);
            }
            LevelManager levelManager = ((LevelManagerAccess) user).skillz$getLevelManager();
            ((ServerPlayerSyncAccess) user)
                    .skillz$addLevelExperience(levelManager.getNextLevelExperience() - ((int) (levelManager.getLevelProgress() * levelManager.getNextLevelExperience())));
        }
        return TypedActionResult.success(stack, world.isClient());
    }

}
