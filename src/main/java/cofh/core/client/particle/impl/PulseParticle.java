package cofh.core.client.particle.impl;

import cofh.core.client.particle.ColorParticle;
import cofh.core.client.particle.CylindricalParticle;
import cofh.core.client.particle.SpriteParticle;
import cofh.core.client.particle.options.ColorParticleOptions;
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
import org.joml.Vector4f;

import javax.annotation.Nonnull;

public class PulseParticle extends ColorParticle {

    public int thickness;
    public boolean shrinking;

    public PulseParticle(ColorParticleOptions data, ClientLevel level, double x, double y, double z, double xDir, double yDir, double zDir) {

        super(data, level, x, y, z, xDir, yDir, zDir);
        oRoll = roll = level.getRandom().nextFloat() * MathHelper.F_TAU;
        thickness = (int) (size * 0.1F * 8.0F * 32F);
        hasPhysics = false;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        Vector4f center = new Vector4f(0, 0, 0, 1).mul(stack.last().pose());

        float x = center.x();
        float y = center.y();
        float z = center.z() + 0.1F;

        float progress = time / duration;
        if (shrinking) {
            progress = 1.0F - progress;
        }
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float radius = easeSin * size * 0.5F;
        int u = (int) (radius * 16.0F * 32F);
        int v = thickness;
        Color c = c0.scaleAlpha(1 - MathHelper.easeInCubic(progress));

        float sin = MathHelper.sin(roll);
        float cos = MathHelper.cos(roll);
        float a = radius * (cos - sin);
        float b = radius * (sin + cos);

        consumer.vertex(x + a, y + b, z).uv(0, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(x - b, y + a, z).uv(0, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(x - a, y - b, z).uv(1, 1).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
        consumer.vertex(x + b, y - a, z).uv(1, 0).color(c.r, c.g, c.b, c.a).uv2(u, v).endVertex();
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
