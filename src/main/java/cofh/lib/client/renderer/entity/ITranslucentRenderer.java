package cofh.lib.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Any entity renderers that have transparency should implement this interface in order to render (mostly) properly.
 * The render type(s) used should have write mask state set to COLOR_WRITE.
 *
 * @author Hekera
 */
public interface ITranslucentRenderer {

    public static void renderTranslucent(MatrixStack stack, float partialTicks, WorldRenderer levelRenderer, Matrix4f projection) {

        Minecraft mc = Minecraft.getInstance();
        EntityRendererManager dispatcher = mc.getEntityRenderDispatcher();
        ClientWorld level = mc.level;
        Vector3d renderPos = mc.gameRenderer.getMainCamera().getPosition();
        IRenderTypeBuffer.Impl buffer = levelRenderer.renderBuffers.bufferSource();

        ClippingHelper clip = levelRenderer.capturedFrustum;
        if (clip == null) {
            clip = new ClippingHelper(stack.last().pose(), projection);
            clip.prepare(renderPos.x, renderPos.y, renderPos.z);
        } else {
            clip.prepare(levelRenderer.frustumPos.x, levelRenderer.frustumPos.y, levelRenderer.frustumPos.z);
        }
        for (Entity entity : level.entitiesForRendering()) {
            EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(entity);
            if (renderer instanceof ITranslucentRenderer && renderer.shouldRender(entity, clip, renderPos.x, renderPos.y, renderPos.z)) {
                double x = MathHelper.lerp(partialTicks, entity.xOld, entity.getX());
                double y = MathHelper.lerp(partialTicks, entity.yOld, entity.getY());
                double z = MathHelper.lerp(partialTicks, entity.zOld, entity.getZ());
                float f = MathHelper.lerp(partialTicks, entity.yRotO, entity.yRot);
                dispatcher.render(entity, x - renderPos.x, y - renderPos.y, z - renderPos.z, f, partialTicks, stack, buffer, dispatcher.getPackedLightCoords(entity, partialTicks));
            }
        }
        buffer.endBatch();
    }

}
