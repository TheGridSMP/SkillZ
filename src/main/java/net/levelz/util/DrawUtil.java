package net.levelz.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class DrawUtil {

    public static boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return pointX >= (double) (x - 1) && pointX < (double) (x + width + 1) && pointY >= (double) (y - 1) && pointY < (double) (y + height + 1);
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, Entity entity) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float g = (float)Math.atan((double)(mouseY / 40.0F));
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20.0F * (float) (Math.PI / 180.0));
        //Quaternionf quaternionf2 = new Quaternionf().rotateX(1).rotateY;
        quaternionf.mul(quaternionf2);
        //float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        if (entity instanceof AbstractMinecartEntity minecart) {
            //minecart.yaw
        }
        //float k = entity.prevHeadYaw;
        //float l = entity.headYaw;
        //entity.bodyYaw = 180.0F + f * 20.0F;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        //entity.headYaw = entity.getYaw();
        //entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        //entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
       // entity.prevHeadYaw = k;
        //entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, Entity entity) {
        context.getMatrices().push();
        context.getMatrices().translate((double)x, (double)y, 50.0);
        context.getMatrices().multiplyPositionMatrix(new Matrix4f().scaling((float)size, (float)size, (float)(-size)));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880));
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

}
