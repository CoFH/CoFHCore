package cofh.lib.client.renderer.entity.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class ArmorModelFullSuit extends BipedModel<LivingEntity> {

    public static ArmorModelFullSuit DEFAULT = new ArmorModelFullSuit(1.0F);
    public static ArmorModelFullSuit LARGE = new ArmorModelFullSuit(1.2F);

    public ArmorModelFullSuit(float modelSize) {

        this(RenderType::getEntityTranslucentCull, modelSize, 0.0F, 64, 32);
    }

    public ArmorModelFullSuit(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {

        this(RenderType::getEntityTranslucentCull, modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
    }

    public ArmorModelFullSuit(Function<ResourceLocation, RenderType> renderTypeIn, float modelSizeIn, float yOffsetIn, int textureWidthIn, int textureHeightIn) {

        super(renderTypeIn, modelSizeIn, yOffsetIn, textureWidthIn, textureHeightIn);

        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn * 0.4F);
        this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + yOffsetIn, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSizeIn * 0.4F);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + yOffsetIn, 0.0F);
    }

    // TODO: Revisit if changes are made to Armor Rendering
    //    @Override
    //    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    //
    //        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    //    }

}
