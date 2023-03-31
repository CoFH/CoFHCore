package cofh.core.client.renderer.entity;

import cofh.core.entity.Knife;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;

public class KnifeRenderer extends EntityRenderer<Knife> {

    protected static final ItemRenderer itemRenderer = RenderHelper.renderItem();

    public KnifeRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(Knife entityIn, float entityYaw, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        poseStackIn.pushPose();
        poseStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 90));
        if (entityIn.inGround()) {
            Vec3 pos = entityIn.position().subtract(Vec3.atCenterOf(entityIn.blockPosition()));
            double y = Math.abs(pos.y);
            if (Math.abs(pos.x) > y || Math.abs(pos.z) > y) {
                poseStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));
            } else if (pos.y <= 0) {
                poseStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));
            }
        } else {
            poseStackIn.mulPose(Vector3f.ZP.rotationDegrees((entityIn.tickCount + partialTicks) * 40));
        }
        poseStackIn.scale(1.25F, 1.25F, 1.25F);
        itemRenderer.renderStatic(entityIn.getPickupItem(), TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, poseStackIn, bufferIn, 0);
        poseStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(Knife entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

}
