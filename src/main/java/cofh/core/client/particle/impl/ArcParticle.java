package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class ArcParticle extends PointToPointParticle {

    protected final float taper;
    protected Vector3f disp;

    private ArcParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        float dx = (float) (ex - sx);
        float dy = (float) (ey - sy);
        float dz = (float) (ez - sz);
        float dist = MathHelper.dist(dx, dy, dz);
        // Partial arc if the distance is short
        if (dist < 4) {
            float frac = dist * 0.25F;
            taper = frac - 1.25F;
            frac = 1 / frac;
            disp = new Vector3f(dx * frac, dy * frac, dz * frac);
        } else {
            taper = 0;
            disp = new Vector3f(dx, dy, dz);
        }
    }

    @Override
    public void tick() {

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float easeCos = MathHelper.cos(progress * MathHelper.F_PI * 0.5F);
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        VFXHelper.alignVertical(stack, MathHelper.ZERO, disp);
        VFXHelper.renderStraightArcs(stack, buffer, packedLight, 2, this.size * (easeCos * 1.5F - 0.5F), 0.015F,
                VFXHelper.getSeedWithTime(seed, age), c0.scaleAlpha(easeCub), c1.scaleAlpha(easeCub), Math.min(easeCos * -2.5F + 1.25F, taper));
    }

    @Override
    public int getLightColor(float pTicks) {

        return 0x00F000F0;
    }

    @Override
    public int getLightColor(float pTicks, double x, double y, double z) {

        return 0x00F000F0;
    }

    @Nonnull
    public static ParticleProvider<BiColorParticleOptions> factory(SpriteSet spriteSet) {

        return ArcParticle::new;
    }

}
