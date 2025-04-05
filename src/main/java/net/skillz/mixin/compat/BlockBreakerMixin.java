package net.skillz.mixin.compat;

/*import draylar.magna.api.BlockBreaker;
import draylar.magna.api.BlockFinder;
import draylar.magna.api.BlockProcessor;
import draylar.magna.api.BreakValidator;*/

//@Mixin(BlockBreaker.class)
public class BlockBreakerMixin {

    /*@Inject(method = "Ldraylar/magna/api/BlockBreaker;breakInRadius(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;IILdraylar/magna/api/BlockFinder;Ldraylar/magna/api/BreakValidator;Ldraylar/magna/api/BlockProcessor;Z)V", at = @At("HEAD"), cancellable = true)
    private static void breakInRadiusMixin(World world, PlayerEntity player, int radius, int depth, BlockFinder finder, BreakValidator breakValidator, BlockProcessor smelter, boolean damageTool,
            CallbackInfo info) {
        if (!world.isClient) {
            ItemStack stack = player.getMainHandStack();

            ArrayList<Object> levelList = LevelLists.customItemList;
            if (!levelList.isEmpty() && levelList.contains(Registries.ITEM.getId(stack.getItem()).toString())) {
                if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, Registries.ITEM.getId(stack.getItem()).toString(), true)) {
                    info.cancel();
                }
            } else {
                levelList = null;
                Item item = stack.getItem();
                if (item instanceof PickaxeItem || item instanceof ShovelItem) {
                    levelList = LevelLists.toolList;
                }
                if (levelList != null)
                    if (!PlayerStatsManager.playerLevelisHighEnough(player, levelList, ((MiningToolItem) item).getMaterial().toString().toLowerCase(), true)) {
                        info.cancel();
                    }
            }
        }
    }*/
}
