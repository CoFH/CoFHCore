package cofh.core.client.renderer.entity;

import cofh.core.common.entity.ElectricArc;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.client.renderer.entity.ITranslucentRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Vector3f;

public class ElectricArcRenderer extends EntityRenderer<ElectricArc> implements ITranslucentRenderer {

    public ElectricArcRenderer(EntityRendererProvider.Context ctx) {

        super(ctx);
    }

    @Override
    public void render(ElectricArc entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {

        stack.pushPose();

        float time = entity.tickCount + partialTicks;

        VFXHelper.alignVertical(stack, new Vector3f(0, 5.9F, 0), new Vector3f(0, -0.9F, 0));
        VFXHelper.renderStraightArcs(stack, buffer, 0x00F000F0, 2, 0.05F, VFXHelper.getSeedWithTime(entity.seed, time),
                Color.WHITE, Color.fromRGBA(0xFFFC52A4), VFXHelper.getTaperOffsetFromTimes(time, entity.getDuration(), 3));

        stack.popPose();

        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ElectricArc entity) {

        return InventoryMenu.BLOCK_ATLAS;
    }

}
