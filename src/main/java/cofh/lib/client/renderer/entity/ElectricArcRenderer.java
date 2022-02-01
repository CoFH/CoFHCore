package cofh.lib.client.renderer.entity;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.entity.ElectricArcEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class ElectricArcRenderer extends EntityRenderer<ElectricArcEntity> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(ID_COFH_CORE + ":textures/entity/lightning_segment.png");
    protected static final RenderType GLOW_RENDER_TYPE = LightningRenderType.lightning(TEXTURE);

    public ElectricArcRenderer(EntityRendererManager manager) {

        super(manager);
    }

    @Override
    public void render(ElectricArcEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        matrixStackIn.pushPose();
        float time = entityIn.tickCount + partialTicks;
        RenderHelper.renderArcs(matrixStackIn, bufferIn.getBuffer(GLOW_RENDER_TYPE), packedLightIn, new Vector3f(0, 10, 0), new Vector3f(0, 0, 0),
                2, 0.4F, entityIn.seed, time, RenderHelper.getTaperOffsetFromTimes(time, ElectricArcEntity.duration, 3));
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricArcEntity entity) {

        return TEXTURE;
    }

    public static class LightningRenderType extends RenderType {

        public LightningRenderType(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_) {

            super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        }

        public static RenderType lightning(ResourceLocation texture) {

            return RenderType.create("lightning",
                    DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
                    RenderType.State.builder().setTextureState(new RenderState.TextureState(texture, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setOutputState(ITEM_ENTITY_TARGET)
                            .setAlphaState(DEFAULT_ALPHA)
                            .setCullState(NO_CULL)
                            .setDepthTestState(LEQUAL_DEPTH_TEST)
                            .createCompositeState(true));
        }

        public static RenderType lightning() {

            return RenderType.create("lightning",
                    DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
                    RenderType.State.builder()
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setOutputState(ITEM_ENTITY_TARGET)
                            .setAlphaState(DEFAULT_ALPHA)
                            .setCullState(NO_CULL)
                            .setDepthTestState(LEQUAL_DEPTH_TEST)
                            .createCompositeState(true));
        }

    }

}
