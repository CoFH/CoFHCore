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

        VFXHelper.renderArcs(stack, bufferIn, packedLightIn, new Vector3f(0, 8, 0), new Vector3f(0, -1, 0),
                3, 0.2F, entityIn.seed, 0xA4FFFC52, VFXHelper.getTaperOffsetFromTimes(time, ElectricArcEntity.defaultDuration, 3));
        //RenderHelper.renderVortex(matrixStackIn, bufferIn, packedLightIn, 0.75F, 1.5F, 0.5F, 75, 0.02F, time, 0.5F);
        //RenderHelper.renderCyclone(matrixStackIn, bufferIn, packedLightIn, 0.75F, 0.5F, 10, 0.02F, time, 0.5F);
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
