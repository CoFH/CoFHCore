package cofh.lib.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;

public class NothingRenderer extends EntityRenderer<Entity> {

    public NothingRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {

    }

    @Override
    public boolean shouldRender(Entity entity, Frustum frustum, double x, double y, double z) {

        return super.shouldRender(entity, frustum, x, y, z);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

}