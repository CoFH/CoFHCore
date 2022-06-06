package cofh.core.event;

import cofh.lib.util.raytracer.VoxelShapeRayTraceResult;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE)
public class HitboxClientEvents {

    private HitboxClientEvents() {

    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void onBlockHighlight(DrawHighlightEvent.HighlightBlock event) {

        BlockRayTraceResult hit = event.getTarget();
        if (hit instanceof VoxelShapeRayTraceResult) {
            VoxelShapeRayTraceResult voxelHit = (VoxelShapeRayTraceResult) hit;
            MatrixStack stack = event.getMatrix();
            BlockPos pos = voxelHit.getBlockPos();
            event.setCanceled(true);

            stack.pushPose();
            stack.translate(pos.getX(), pos.getY(), pos.getZ());

            bufferShapeHitBox(stack, event.getBuffers(), event.getInfo(), voxelHit.shape);

            stack.popPose();
        }
    }

    // region HELPERS
    private static void bufferShapeHitBox(MatrixStack pStack, IRenderTypeBuffer buffers, ActiveRenderInfo renderInfo, VoxelShape shape) {

        Vector3d eye = renderInfo.getPosition();
        pStack.translate((float) -eye.x, (float) -eye.y, (float) -eye.z);
        bufferShapeOutline(buffers.getBuffer(RenderType.lines()), pStack.last().pose(), shape, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private static void bufferShapeOutline(IVertexBuilder builder, Matrix4f mat, VoxelShape shape, float r, float g, float b, float a) {

        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            builder.vertex(mat, (float) x1, (float) y1, (float) z1).color(r, g, b, a).endVertex();
            builder.vertex(mat, (float) x2, (float) y2, (float) z2).color(r, g, b, a).endVertex();
        });
    }
    // endregion
}
