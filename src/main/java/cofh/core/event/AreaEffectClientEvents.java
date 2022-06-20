package cofh.core.event;

import cofh.core.capability.templates.AreaEffectItemWrapper;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static cofh.core.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.core.util.helpers.AreaEffectHelper.validAreaEffectItem;
import static cofh.core.util.helpers.AreaEffectHelper.validAreaEffectMiningItem;
import static cofh.lib.util.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE)
public class AreaEffectClientEvents {

    private AreaEffectClientEvents() {

    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void renderBlockHighlights(DrawSelectionEvent.HighlightBlock event) {

        if (event.isCanceled()) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!validAreaEffectItem(stack)) {
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(event.getTarget().getBlockPos(), player);

        LevelRenderer levelRenderer = event.getLevelRenderer();
        PoseStack matrix = event.getPoseStack();
        VertexConsumer vertexBuilder = levelRenderer.renderBuffers.bufferSource().getBuffer(RenderType.lines());
        Entity viewEntity = camera.getEntity();
        Level world = player.level;

        Vec3 vec3d = camera.getPosition();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();

        matrix.pushPose();
        for (BlockPos pos : areaBlocks) {
            if (world.getWorldBorder().isWithinBounds(pos)) {
                levelRenderer.renderHitOutline(matrix, vertexBuilder, viewEntity, d0, d1, d2, pos, world.getBlockState(pos));
            }
        }
        matrix.popPose();

        MultiPlayerGameMode gamemode = Minecraft.getInstance().gameMode;
        if (gamemode == null || !gamemode.isDestroying()) {
            return;
        }
        if (!validAreaEffectMiningItem(stack)) {
            return;
        }
        drawBlockDamageTexture(gamemode, event.getLevelRenderer(), event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.getCommandSenderWorld(), areaBlocks);
    }

    // region HELPERS
    private static void drawBlockDamageTexture(MultiPlayerGameMode gameMode, LevelRenderer levelRenderer, PoseStack posestack, Camera camera, Level level, List<BlockPos> areaBlocks) {

        double d0 = camera.getPosition().x;
        double d1 = camera.getPosition().y;
        double d2 = camera.getPosition().z;

        int progress = (int) (gameMode.destroyProgress * 10.0F) - 1;
        if (progress < 0 || progress > 10) {
            return;
        }
        progress = Math.min(progress + 1, 9); // Ensure that for whatever reason the progress level doesn't go OOB.

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer vertexBuilder = levelRenderer.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress));

        for (BlockPos pos : areaBlocks) {
            posestack.pushPose();
            posestack.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
            PoseStack.Pose matrixEntry = posestack.last();
            VertexConsumer matrixBuilder = new SheetedDecalTextureGenerator(vertexBuilder, matrixEntry.pose(), matrixEntry.normal());
            dispatcher.renderBreakingTexture(level.getBlockState(pos), pos, level, posestack, matrixBuilder);
            posestack.popPose();
        }
    }
    // endregion
}
