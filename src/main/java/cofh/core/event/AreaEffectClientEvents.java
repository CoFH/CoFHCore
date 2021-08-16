package cofh.core.event;

import cofh.core.client.CoreRenderType;
import cofh.lib.capability.templates.AreaEffectItemWrapper;
import cofh.lib.tileentity.IAreaEffectTile;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import static cofh.lib.capability.CapabilityAreaEffect.AREA_EFFECT_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectItem;
import static cofh.lib.util.helpers.AreaEffectHelper.validAreaEffectMiningItem;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ID_COFH_CORE)
public class AreaEffectClientEvents {

    private static final Set<IAreaEffectTile> AREA_EFFECT_TILES = Collections.newSetFromMap(new WeakHashMap<>());

    private AreaEffectClientEvents() {

    }

    public static void registerAreaEffectTile(IAreaEffectTile tile) {

        AREA_EFFECT_TILES.add(tile);
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
        ItemStack stack = player.getHeldItemMainhand();
        if (!validAreaEffectItem(stack)) {
            return;
        }
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        ImmutableList<BlockPos> areaBlocks = stack.getCapability(AREA_EFFECT_ITEM_CAPABILITY).orElse(new AreaEffectItemWrapper(stack)).getAreaEffectBlocks(event.getTarget().getPos(), player);

        WorldRenderer worldRender = event.getContext();
        MatrixStack matrix = event.getMatrix();
        IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getBufferSource().getBuffer(RenderType.getLines());
        Entity viewEntity = renderInfo.getRenderViewEntity();
        World world = player.world;

        Vector3d vec3d = renderInfo.getProjectedView();
        double d0 = vec3d.getX();
        double d1 = vec3d.getY();
        double d2 = vec3d.getZ();

        matrix.push();
        for (BlockPos pos : areaBlocks) {
            if (world.getWorldBorder().contains(pos)) {
                worldRender.drawSelectionBox(matrix, vertexBuilder, viewEntity, d0, d1, d2, pos, world.getBlockState(pos));
            }
        }
        matrix.pop();

        PlayerController controller = Minecraft.getInstance().playerController;
        if (controller == null || !controller.isHittingBlock) {
            return;
        }
        if (!validAreaEffectMiningItem(stack)) {
            return;
        }
        drawBlockDamageTexture(controller, event.getContext(), event.getMatrix(), Minecraft.getInstance().gameRenderer.getActiveRenderInfo(), player.getEntityWorld(), areaBlocks);
    }

    // region HELPERS
    private static void drawBlockDamageTexture(PlayerController controller, WorldRenderer worldRender, MatrixStack matrixStackIn, ActiveRenderInfo renderInfo, World world, List<BlockPos> areaBlocks) {

        double d0 = renderInfo.getProjectedView().x;
        double d1 = renderInfo.getProjectedView().y;
        double d2 = renderInfo.getProjectedView().z;

        int progress = (int) (controller.curBlockDamageMP * 10.0F) - 1;
        if (progress < 0 || progress > 10) {
            return;
        }
        progress = Math.min(progress + 1, 9); // Ensure that for whatever reason the progress level doesn't go OOB.

        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(progress));

        for (BlockPos pos : areaBlocks) {
            matrixStackIn.push();
            matrixStackIn.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
            MatrixStack.Entry matrixEntry = matrixStackIn.getLast();
            IVertexBuilder matrixBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, matrixEntry.getMatrix(), matrixEntry.getNormal());
            dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
            matrixStackIn.pop();
        }
    }
    // endregion

    // TODO: Make an item that uses this.
    // region TUTORIAL
    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player.getHeldItemMainhand().getItem() == Items.NETHER_STAR) {
            locateTileEntities(player, event.getMatrixStack());
        }
    }

    private static void blueLine(IVertexBuilder builder, Matrix4f positionMatrix, BlockPos pos, float dx1, float dy1, float dz1, float dx2, float dy2, float dz2) {

        builder.pos(positionMatrix, pos.getX() + dx1, pos.getY() + dy1, pos.getZ() + dz1)
                .color(0.0f, 0.0f, 1.0f, 1.0f)
                .endVertex();
        builder.pos(positionMatrix, pos.getX() + dx2, pos.getY() + dy2, pos.getZ() + dz2)
                .color(0.0f, 0.0f, 1.0f, 1.0f)
                .endVertex();
    }

    public static boolean playerWithinDistance(BlockPos pos, PlayerEntity player, double distanceSq) {

        return pos.distanceSq(player.getPositionVec(), true) <= distanceSq;
    }

    private static void locateTileEntities(ClientPlayerEntity player, MatrixStack matrixStack) {

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder builder = buffer.getBuffer(CoreRenderType.OVERLAY_LINES);

        BlockPos playerPos = player.getPosition();
        int px = playerPos.getX();
        int py = playerPos.getY();
        int pz = playerPos.getZ();
        World world = player.getEntityWorld();

        matrixStack.push();

        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f positionMatrix = matrixStack.getLast().getMatrix();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (IAreaEffectTile tile : AREA_EFFECT_TILES) {
            if (!tile.canPlayerAccess(player) || !playerWithinDistance(tile.pos(), player, 576)) {
                continue;
            }
            AxisAlignedBB area = tile.getArea();

            pos.setPos(area.minX, area.minY, area.minZ);
            float lenX = (float) (area.maxX - area.minX);
            float lenY = (float) (area.maxY - area.minY);
            float lenZ = (float) (area.maxZ - area.minZ);

            blueLine(builder, positionMatrix, pos, 0, 0, 0, lenX, 0, 0);
            blueLine(builder, positionMatrix, pos, 0, lenY, 0, lenX, lenY, 0);
            blueLine(builder, positionMatrix, pos, 0, 0, lenZ, lenX, 0, lenZ);
            blueLine(builder, positionMatrix, pos, 0, lenY, lenZ, lenX, lenY, lenZ);

            blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, 0, lenZ);
            blueLine(builder, positionMatrix, pos, lenX, 0, 0, lenX, 0, lenZ);
            blueLine(builder, positionMatrix, pos, 0, lenY, 0, 0, lenY, lenZ);
            blueLine(builder, positionMatrix, pos, lenX, lenY, 0, lenX, lenY, lenZ);

            blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, lenY, 0);
            blueLine(builder, positionMatrix, pos, lenX, 0, 0, lenX, lenY, 0);
            blueLine(builder, positionMatrix, pos, 0, 0, lenZ, 0, lenY, lenZ);
            blueLine(builder, positionMatrix, pos, lenX, 0, lenZ, lenX, lenY, lenZ);
        }

        //        for (int dx = -10; dx <= 10; ++dx) {
        //            for (int dy = -10; dy <= 10; ++dy) {
        //                for (int dz = -10; dz <= 10; ++dz) {
        //                    pos.setPos(px + dx, py + dy, pz + dz);
        //                    if (world.getTileEntity(pos) instanceof IAreaEffectTile) {
        //                        AxisAlignedBB area = ((IAreaEffectTile) world.getTileEntity(pos)).getArea();
        //
        //                        pos.setPos(area.minX, area.minY, area.minZ);
        //                        float lenX = (float) (area.maxX - area.minX);
        //                        float lenY = (float) (area.maxY - area.minY);
        //                        float lenZ = (float) (area.maxZ - area.minZ);
        //
        //                        blueLine(builder, positionMatrix, pos, 0, 0, 0, lenX, 0, 0);
        //                        blueLine(builder, positionMatrix, pos, 0, lenY, 0, lenX, lenY, 0);
        //                        blueLine(builder, positionMatrix, pos, 0, 0, lenZ, lenX, 0, lenZ);
        //                        blueLine(builder, positionMatrix, pos, 0, lenY, lenZ, lenX, lenY, lenZ);
        //
        //                        blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, 0, lenZ);
        //                        blueLine(builder, positionMatrix, pos, lenX, 0, 0, lenX, 0, lenZ);
        //                        blueLine(builder, positionMatrix, pos, 0, lenY, 0, 0, lenY, lenZ);
        //                        blueLine(builder, positionMatrix, pos, lenX, lenY, 0, lenX, lenY, lenZ);
        //
        //                        blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, lenY, 0);
        //                        blueLine(builder, positionMatrix, pos, lenX, 0, 0, lenX, lenY, 0);
        //                        blueLine(builder, positionMatrix, pos, 0, 0, lenZ, 0, lenY, lenZ);
        //                        blueLine(builder, positionMatrix, pos, lenX, 0, lenZ, lenX, lenY, lenZ);
        //                    }
        //                }
        //            }
        //        }
        matrixStack.pop();
        RenderSystem.disableDepthTest();
        buffer.finish(CoreRenderType.OVERLAY_LINES);
    }
    // endregion
}
