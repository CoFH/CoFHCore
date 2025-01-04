package cofh.core.client.particle.impl;

import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.options.CylindricalParticleOptions;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.Color;
import cofh.core.util.helpers.vfx.RenderTypes;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

public class RingParticle extends CylindricalParticle {

    public boolean shrinking;

    public RingParticle(CylindricalParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        oRoll = roll = level.getRandom().nextFloat() * MathHelper.F_TAU;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLightIn, float time, float pTicks) {

        if (!rotation.equals(new Quaternionf())) {
            stack.mulPose(rotation);
        }
        float progress = time / duration;
        if (shrinking) {
            progress = 1.0F - progress;
        }
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float radius = easeSin * size * 0.5F;
        int u = (int) (radius * 16.0F * 32F);
        int v = (int) (height * 8.0F * 32F);
        Color c = c0.scaleAlpha(1 - MathHelper.easeInCubic(progress));

        float sin = MathHelper.sin(roll);
        float cos = MathHelper.cos(roll);
        float a = radius * (cos - sin);
        float b = radius * (sin + cos);

        Matrix4f pose = stack.last().pose();
        consumer.vertex(pose, a, 0.2F, b).uv(0, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, b, 0.2F, -a).uv(0, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, -a, 0.2F, -b).uv(1, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, -b, 0.2F, a).uv(1, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();

        consumer.vertex(pose, a, 0.2F, b).uv(0, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, -b, 0.2F, a).uv(0, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, -a, 0.2F, -b).uv(1, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(pose, b, 0.2F, -a).uv(1, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_RING;
    }

    @Override
    public int getLightColor(float partialTicks) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Override
    protected void setLifetime(float duration, float delay) {

        this.shrinking = duration < 0;
        super.setLifetime(Math.abs(duration), delay);
    }

}
