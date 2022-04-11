package cofh.lib.client.renderer.entity;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.entity.ElectricArcEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ElectricArcRenderer extends EntityRenderer<ElectricArcEntity> implements ITranslucentRenderer {

    public ElectricArcRenderer(EntityRendererManager manager) {

        super(manager);
    }

    @Override
    public void render(ElectricArcEntity entityIn, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {

        stack.pushPose();
        float time = entityIn.tickCount + partialTicks;

        VFXHelper.transformVertical(stack, new Vector3f(0, 7.9F, 0), new Vector3f(0, -0.9F, 0));
        VFXHelper.renderStraightArcs(stack, bufferIn, packedLightIn, 2, 0.02F, VFXHelper.getSeedWithTime(entityIn.seed, time), 0xA4FFFC52, 0);
        //stack.scale(1, 1, 3);
        //stack.mulPose(Vector3f.YP.rotationDegrees(time * 10));
        //stack.mulPose(Vector3f.ZP.rotationDegrees(time * 5));
        //VFXHelper.renderCyclone(stack, bufferIn.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, 10, 0.03F, time * 0.01F, 0.2F);
        //builder.vertex(pose,0, 1, 0).color(255, 255, 255, 128).endVertex();
        //VFXHelper.transformVertical(stack, new Vector3f(1, 1, 0));
        //VFXHelper.renderCyclone(stack, bufferIn.getBuffer(RenderTypes.FLAT_TRANSLUCENT), packedLightIn, 1, 0.03F, 100, 0.5F);

        stack.popPose();

        super.render(entityIn, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricArcEntity entity) {

        return PlayerContainer.BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(ElectricArcEntity entity, ClippingHelper clip, double x, double y, double z) {

        return super.shouldRender(entity, clip, x, y, z);
    }

}
