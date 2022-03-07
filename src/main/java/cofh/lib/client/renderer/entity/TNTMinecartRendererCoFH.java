package cofh.lib.client.renderer.entity;

import cofh.lib.entity.AbstractTNTMinecartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TNTMinecartRendererCoFH extends MinecartRenderer<AbstractTNTMinecartEntity> {

    public TNTMinecartRendererCoFH(EntityRendererProvider.Context renderManagerIn, ModelLayerLocation modelLayerLocation) {

        super(renderManagerIn, modelLayerLocation);
    }

    @Override
    protected void renderMinecartContents(AbstractTNTMinecartEntity entityIn, float partialTicks, BlockState stateIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        int i = entityIn.getFuseTicks();
        if (i > -1 && (float) i - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) i - partialTicks + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            matrixStackIn.scale(f1, f1, f1);
        }
        TntMinecartRenderer.renderWhiteSolidBlock(stateIn, matrixStackIn, bufferIn, packedLightIn, i > -1 && i / 5 % 2 == 0);
    }

}
