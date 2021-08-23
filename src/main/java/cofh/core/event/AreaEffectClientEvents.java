package cofh.core.event;

import cofh.lib.capability.templates.AreaEffectItemWrapper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectItem;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectMiningItem;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ID_COFH_CORE)
public class AreaEffectClientEvents {

    private AreaEffectClientEvents() {

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {

        if (event.isCanceled()) {
            return;
        }
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!validAreaEffectItem(stack)) {
            return;
        }
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(event.getTarget().getBlockPos(), player);

        WorldRenderer worldRender = event.getContext();
        MatrixStack matrix = event.getMatrix();
        IVertexBuilder vertexBuilder = worldRender.renderBuffers.bufferSource().getBuffer(RenderType.lines());
        Entity viewEntity = renderInfo.getEntity();
        World world = player.level;

        Vector3d vec3d = renderInfo.getPosition();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();

        matrix.pushPose();
        for (BlockPos pos : areaBlocks) {
            if (world.getWorldBorder().isWithinBounds(pos)) {
                worldRender.renderHitOutline(matrix, vertexBuilder, viewEntity, d0, d1, d2, pos, world.getBlockState(pos));
            }
        }
        matrix.popPose();

        PlayerController controller = Minecraft.getInstance().gameMode;
        if (controller == null || !controller.isDestroying) {
            return;
        }
        if (!validAreaEffectMiningItem(stack)) {
            return;
        }
        drawBlockDamageTexture(controller, event.getContext(), event.getMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.getCommandSenderWorld(), areaBlocks);
    }

    // region HELPERS
    private static void drawBlockDamageTexture(PlayerController controller, WorldRenderer worldRender, MatrixStack matrixStackIn, ActiveRenderInfo renderInfo, World world, List<BlockPos> areaBlocks) {

        double d0 = renderInfo.getPosition().x;
        double d1 = renderInfo.getPosition().y;
        double d2 = renderInfo.getPosition().z;

        int progress = (int) (controller.destroyProgress * 10.0F) - 1;
        if (progress < 0 || progress > 10) {
            return;
        }
        progress = Math.min(progress + 1, 9); // Ensure that for whatever reason the progress level doesn't go OOB.

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        IVertexBuilder vertexBuilder = worldRender.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress));

        for (BlockPos pos : areaBlocks) {
            matrixStackIn.pushPose();
            matrixStackIn.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
            MatrixStack.Entry matrixEntry = matrixStackIn.last();
            IVertexBuilder matrixBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, matrixEntry.pose(), matrixEntry.normal());
            dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
            matrixStackIn.popPose();
        }
    }
    // endregion
}
