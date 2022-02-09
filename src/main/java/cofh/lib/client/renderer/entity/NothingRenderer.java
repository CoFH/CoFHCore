package cofh.lib.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;

public class NothingRenderer extends EntityRenderer<Entity> {

    public NothingRenderer(EntityRendererManager manager) {

        super(manager);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {

    }

    @Override
    public boolean shouldRender(Entity entity, ClippingHelper helper, double p_225626_3_, double p_225626_5_, double p_225626_7_) {

        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {

        return PlayerContainer.BLOCK_ATLAS;
    }

}