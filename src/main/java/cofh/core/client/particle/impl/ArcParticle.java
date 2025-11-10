package cofh.core.client.particle.impl;

import cofh.core.client.particle.PointToPointParticle;
import cofh.core.client.particle.options.BiColorParticleOptions;
import cofh.core.client.TransientLightManager;
import cofh.core.common.config.CoreClientConfig;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class ArcParticle extends PointToPointParticle {

    protected Vec3 end;
    protected float taper;
    protected Vector3f disp;

    public ArcParticle(BiColorParticleOptions data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(data, level, sx, sy, sz, ex, ey, ez);
        end = new Vec3(ex, ey, ez);
    }

    public ArcParticle(BiColorParticleOptions data, ClientLevel level, Vec3 start, Vec3 end) {

        this(data, level, start.x, start.y, start.z, end.x, end.y, end.z);
    }

    @Override
    public void tick() {

        if (isAlive() && CoreClientConfig.particleDynamicLighting.get() && this.age >= this.delay) {
            int light = getDynamicLightLevel();
            LongList path = getPath();
            for (int i = path.size() - 1; i >= 0; --i) {
                TransientLightManager.addLight(level, path.getLong(i), light);
            }
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    protected abstract LongList getPath();

    protected void recalcDisplacement(double sx, double sy, double sz, double ex, double ey, double ez) {

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

    protected int getDynamicLightLevel() {

        return 8;
    }

    protected LongList traversePath(double sx, double sy, double sz, double ex, double ey, double ez) {

        LongList path = new LongArrayList();
        int i = Mth.floor(sx);
        int j = Mth.floor(sy);
        int k = Mth.floor(sz);
        path.add(BlockPos.asLong(i, j, k));
        double dx = ex - sx;
        double dy = ey - sy;
        double dz = ez - sz;
        int signX = Mth.sign(dx);
        int signY = Mth.sign(dy);
        int signZ = Mth.sign(dz);
        double incrX = signX == 0 ? Double.MAX_VALUE : signX / dx;
        double incrY = signY == 0 ? Double.MAX_VALUE : signY / dy;
        double incrZ = signZ == 0 ? Double.MAX_VALUE : signZ / dz;
        double remX = incrX * (signX > 0 ? 1.0D - Mth.frac(sx) : Mth.frac(sx));
        double remY = incrY * (signY > 0 ? 1.0D - Mth.frac(sy) : Mth.frac(sy));
        double remZ = incrZ * (signZ > 0 ? 1.0D - Mth.frac(sz) : Mth.frac(sz));

        while (remX <= 1.0D || remY <= 1.0D || remZ <= 1.0D) {
            if (remX < remY) {
                if (remX < remZ) {
                    i += signX;
                    remX += incrX;
                } else {
                    k += signZ;
                    remZ += incrZ;
                }
            } else if (remY < remZ) {
                j += signY;
                remY += incrY;
            } else {
                k += signZ;
                remZ += incrZ;
            }
            path.add(BlockPos.asLong(i, j, k));
        }
        return path;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, VertexConsumer consumer, int packedLight, float time, float pTicks) {

        float progress = time / duration;
        float easeCos = MathHelper.cos(progress * MathHelper.F_HALF_PI);
        float easeCub = 1.0F - MathHelper.easeInCubic(progress);
        VFXHelper.alignVertical(stack, MathHelper.ZERO, disp);
        VFXHelper.renderStraightArcs(stack, buffer, packedLight, 2, this.size * (easeCub * 0.75F + 0.25F), 0.015F,
                VFXHelper.getSeedWithTime(seed, age), c0.scaleAlpha(easeCub), c1.scaleAlpha(easeCub), Math.min(easeCos * -2.5F + 1.25F, taper));
    }

    @Override
    public int getLightColor(float pTicks) {

        return RenderHelper.FULL_BRIGHT;
    }

    @Override
    public int getLightColor(float pTicks, double x, double y, double z) {

        return RenderHelper.FULL_BRIGHT;
    }

}
