package cofh.lib.client.renderer.entity;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.entity.KnifeEntity;
<<<<<<< HEAD
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
=======
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class KnifeRenderer extends EntityRenderer<KnifeEntity> {

    protected static final ItemRenderer itemRenderer = RenderHelper.renderItem();

    public KnifeRenderer(EntityRendererProvider.Context manager) {

        super(manager);
    }

    @Override
    public void render(KnifeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 90));
        if (entityIn.inGround()) {
            Vec3 pos = entityIn.position().subtract(Vec3.atCenterOf(entityIn.blockPosition()));
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
        itemRenderer.renderStatic(entityIn.getPickupItem(), TransformType.GROUND, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, 0);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(KnifeEntity entity) {

        return TextureAtlas.LOCATION_BLOCKS;
    }

}
