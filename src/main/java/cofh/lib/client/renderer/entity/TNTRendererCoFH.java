package cofh.lib.client.renderer.entity;

import cofh.lib.entity.PrimedTntCoFH;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn (Dist.CLIENT)
public class TNTRendererCoFH extends EntityRenderer<PrimedTntCoFH> {

    public TNTRendererCoFH(EntityRendererProvider.Context ctx) {

        super(ctx);
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(PrimedTntCoFH entityIn, float entityYaw, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferIn, int packedLightIn) {

        poseStackIn.pushPose();
        poseStackIn.translate(0.0D, 0.5D, 0.0D);
        if ((float) entityIn.getFuse() - partialTicks + 1.0F < 10.0F) {
            float f = 1.0F - ((float) entityIn.getFuse() - partialTicks + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            poseStackIn.scale(f1, f1, f1);
        }
        poseStackIn.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        poseStackIn.translate(-0.5D, -0.5D, 0.5D);
        poseStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        renderFlash(entityIn.getBlock().defaultBlockState(), poseStackIn, bufferIn, packedLightIn, entityIn.getFuse() / 5 % 2 == 0);
        poseStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimedTntCoFH entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

    public static void renderFlash(BlockState blockStateIn, PoseStack poseStackIn, MultiBufferSource renderTypeBuffer, int combinedLight, boolean doFullBright) {

        int i;
        if (doFullBright) {
            i = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
        } else {
            i = OverlayTexture.NO_OVERLAY;
        }
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockStateIn, poseStackIn, renderTypeBuffer, combinedLight, i);
    }

}
