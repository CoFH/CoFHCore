package cofh.lib.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Any entity renderers that have transparency should implement this interface in order to render (mostly) properly.
 * The render type(s) used should have write mask state set to COLOR_WRITE.
 *
 * @author Hekera
 */
public interface ITranslucentRenderer {

    static void renderTranslucent(PoseStack stack, float partialTicks, LevelRenderer levelRenderer, Matrix4f projection) {

        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        ClientLevel level = mc.level;
        Vec3 renderPos = mc.gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = levelRenderer.renderBuffers.bufferSource();

        Frustum clip = levelRenderer.capturedFrustum;
        if (clip == null) {
            clip = new Frustum(stack.last().pose(), projection);
            clip.prepare(renderPos.x, renderPos.y, renderPos.z);
        } else {
            clip.prepare(levelRenderer.frustumPos.x, levelRenderer.frustumPos.y, levelRenderer.frustumPos.z);
        }
        for (Entity entity : level.entitiesForRendering()) {
            EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(entity);
            if (renderer instanceof ITranslucentRenderer && renderer.shouldRender(entity, clip, renderPos.x, renderPos.y, renderPos.z)) {
                double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
                double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
                double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
                float f = Mth.lerp(partialTicks, entity.yRotO, entity.yRot);
                dispatcher.render(entity, x - renderPos.x, y - renderPos.y, z - renderPos.z, f, partialTicks, stack, buffer, dispatcher.getPackedLightCoords(entity, partialTicks));
            }
        }
        buffer.endBatch();
    }

}
