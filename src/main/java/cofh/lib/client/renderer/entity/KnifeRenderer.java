package cofh.lib.client.renderer.entity;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.entity.KnifeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class KnifeRenderer extends EntityRenderer<KnifeEntity> {

    protected static final ItemRenderer itemRenderer = RenderHelper.renderItem();

    public KnifeRenderer(EntityRendererManager manager) {

        super(manager);
    }

    @Override
    public void render(KnifeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot) + 90));
        if (entityIn.inGround()) {
            Vector3d pos = entityIn.position().subtract(Vector3d.atCenterOf(entityIn.blockPosition()));
            double y = Math.abs(pos.y);
            if (Math.abs(pos.x) > y || Math.abs(pos.z) > y) {
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(90));
            } else if (pos.y <= 0) {
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180));
            }
        } else {
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees((entityIn.tickCount + partialTicks) * 40));
        }
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
        itemRenderer.renderStatic(entityIn.getPickupItem(), ItemCameraTransforms.TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(KnifeEntity entity) {

        return PlayerContainer.BLOCK_ATLAS;
    }

}
