package net.skillz.mixin.player;

import net.skillz.access.LevelManagerAccess;
import net.skillz.init.ConfigInit;
import net.skillz.level.LevelManager;
import net.skillz.level.SkillPoints;
import net.skillz.util.PacketHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getBossBarManager()Lnet/minecraft/entity/boss/BossBarManager;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info, GameProfile gameProfile, UserCache userCache, String string, NbtCompound optional, RegistryKey<World> registryKey, ServerWorld serverWorld, ServerWorld serverWorld2) {
       if (optional == null || optional.isEmpty()) {
           LevelManager levelManager = ((LevelManagerAccess) player).skillz$getLevelManager();

           for (SkillPoints ps : LevelManager.POINTS.values()) {
               if (ps.start() > 0) levelManager.setSkillPoints(ps.id(), ps.start());
           }

           PacketHelper.updateLevels(player);
       }
    }
}