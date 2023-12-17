package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.common.TransientLightManager;
import cofh.core.common.config.CoreClientConfig;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class ArcParticle extends PointToPointParticle {

    protected final float taper;
    protected Vector3f disp;
    protected LongList path;

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
        this.path = traversePath();
    }

    @Override
    public void tick() {

        if (CoreClientConfig.particleDynamicLighting.get() && this.age >= this.delay) {
            int light = getDynamicLightLevel();
            for (int i = path.size() - 1; i >= 0; --i) {
                TransientLightManager.addLight(path.getLong(i), light);
            }
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    protected int getDynamicLightLevel() {

        return 8;
    }

    protected LongList traversePath() {

        LongList path = new LongArrayList();
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        path.add(BlockPos.asLong(i, j, k));
        double dx = disp.x;
        double dy = disp.y;
        double dz = disp.z;
        int signX = Mth.sign(dx);
        int signY = Mth.sign(dy);
        int signZ = Mth.sign(dz);
        double d9 = signX == 0 ? Double.MAX_VALUE : (double) signX / dx;
        double d10 = signY == 0 ? Double.MAX_VALUE : (double) signY / dy;
        double d11 = signZ == 0 ? Double.MAX_VALUE : (double) signZ / dz;
        double d12 = d9 * (signX > 0 ? 1.0D - Mth.frac(x) : Mth.frac(x));
        double d13 = d10 * (signY > 0 ? 1.0D - Mth.frac(y) : Mth.frac(y));
        double d14 = d11 * (signZ > 0 ? 1.0D - Mth.frac(z) : Mth.frac(z));

        while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
            if (d12 < d13) {
                if (d12 < d14) {
                    i += signX;
                    d12 += d9;
                } else {
                    k += signZ;
                    d14 += d11;
                }
            } else if (d13 < d14) {
                j += signY;
                d13 += d10;
            } else {
                k += signZ;
                d14 += d11;
            }
            path.add(BlockPos.asLong(i, j, k));
        }
        return path;
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
