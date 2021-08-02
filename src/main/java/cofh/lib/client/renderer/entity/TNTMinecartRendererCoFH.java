package cofh.lib.client.renderer.entity;

import cofh.lib.entity.AbstractTNTMinecartEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.TNTMinecartRenderer;
import net.minecraft.util.math.MathHelper;

public class TNTMinecartRendererCoFH extends MinecartRenderer<AbstractTNTMinecartEntity> {

    public TNTMinecartRendererCoFH(EntityRendererManager renderManagerIn) {

        super(renderManagerIn);
    }

    @Override
    protected void renderBlockState(AbstractTNTMinecartEntity entityIn, float partialTicks, BlockState stateIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {

        int i = entityIn.getFuseTicks();
        if (i > -1 && (float) i - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) i - partialTicks + 1.0F) / 10.0F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            matrixStackIn.scale(f1, f1, f1);
        }
        TNTMinecartRenderer.renderTntFlash(stateIn, matrixStackIn, bufferIn, packedLightIn, i > -1 && i / 5 % 2 == 0);
    }

}
