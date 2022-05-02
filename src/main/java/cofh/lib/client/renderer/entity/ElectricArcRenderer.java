package cofh.lib.client.renderer.entity;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.entity.ElectricArcEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class ElectricArcRenderer extends EntityRenderer<ElectricArcEntity> implements ITranslucentRenderer {

    public ElectricArcRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(ElectricArcEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {

        stack.pushPose();

        float time = entity.tickCount + partialTicks;
        VFXHelper.transformVertical(stack, new Vector3f(0, 7.9F, 0), new Vector3f(0, -0.9F, 0));
        VFXHelper.renderStraightArcs(stack, buffer, packedLight, 2, 0.02F, VFXHelper.getSeedWithTime(entity.seed, time), 0xFFFC52A4, VFXHelper.getTaperOffsetFromTimes(time, ElectricArcEntity.defaultDuration, 3));

        stack.popPose();

        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricArcEntity entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

    @Override
    public boolean shouldRender(ElectricArcEntity entity, Frustum clip, double x, double y, double z) {

        return super.shouldRender(entity, clip, x, y, z);
    }

}
