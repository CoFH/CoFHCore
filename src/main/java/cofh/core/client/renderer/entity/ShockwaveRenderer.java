package cofh.core.client.renderer.entity;

import cofh.core.common.entity.Shockwave;
import cofh.core.util.helpers.vfx.VFXHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ShockwaveRenderer extends EntityRenderer<Shockwave> {

    public ShockwaveRenderer(EntityRendererProvider.Context pContext) {

        super(pContext);
    }

    @Override
    public void render(Shockwave entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {

        float time = entity.tickCount + partialTicks;
        float duration = entity.getDuration() + 5;
        float size = entity.getBbWidth();
        VFXHelper.renderShockwave(stack, buffer, entity.level, entity.blockPosition(), time * (size * 0.5F + 5) / duration, size, entity.getBbWidth());
    }

    @Override
    public ResourceLocation getTextureLocation(Shockwave pEntity) {

        return null;
    }

}
