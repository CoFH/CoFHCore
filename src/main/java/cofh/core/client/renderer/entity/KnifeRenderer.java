package cofh.core.client.renderer.entity;

import cofh.core.entity.ThrownKnife;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class KnifeRenderer extends EntityRenderer<ThrownKnife> {

    protected static final ItemRenderer itemRenderer = RenderHelper.renderItem();

    public KnifeRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(ThrownKnife entityIn, float entityYaw, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        poseStackIn.pushPose();
        poseStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 90));
        if (entityIn.inGround()) {
            Vec3 pos = entityIn.position().subtract(Vec3.atCenterOf(entityIn.blockPosition()));
            double y = Math.abs(pos.y);
            if (Math.abs(pos.x) > y || Math.abs(pos.z) > y) {
                poseStackIn.mulPose(Axis.ZP.rotationDegrees(90));
            } else if (pos.y <= 0) {
                poseStackIn.mulPose(Axis.ZP.rotationDegrees(180));
            }
        } else {
            poseStackIn.mulPose(Axis.ZP.rotationDegrees((entityIn.tickCount + partialTicks) * 40));
        }
        poseStackIn.scale(1.25F, 1.25F, 1.25F);
        itemRenderer.renderStatic(entityIn.getPickupItem(), ItemDisplayContext.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, poseStackIn, bufferIn, entityIn.level, 0);
        poseStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownKnife entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

}
