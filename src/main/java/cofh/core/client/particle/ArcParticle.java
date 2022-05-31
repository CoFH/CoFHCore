package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.entity.ElectricArc;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class ArcParticle extends LevelMatrixStackParticle {

    protected final Vector3f dest;
    protected final float taper;
    protected final int seed;

    private ArcParticle(ClientLevel levelIn, double sx, double sy, double sz, double ex, double ey, double ez) {

        super(levelIn, sx, sy, sz, 0, 0, 0);
        float dx = (float) (ex - sx);
        float dy = (float) (ey - sy);
        float dz = (float) (ez - sz);
        this.bbWidth = Math.max(Math.abs(dx), Math.abs(dz));
        this.bbHeight = Math.abs(dy);
        this.setBoundingBox(new AABB(sx, sy, sz, ex, ey, ez));
        float distSqr = MathHelper.dist(dx, dy, dz);
        if (distSqr < 4) {
            float frac = distSqr * 0.25F;
            taper = frac - 1.25F;
            frac = 1 / frac;
            dest = new Vector3f(dx * frac, dy * frac, dz * frac);
        } else {
            taper = 0;
            dest = new Vector3f(dx, dy, dz);
        }

        this.lifetime = 7 + random.nextInt(3);
        this.seed = random.nextInt();
        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float partialTicks) {

        float progress = (age + partialTicks) / lifetime;
        float easeCos = MathHelper.cos(progress * MathHelper.F_PI * 0.5F);
        VFXHelper.transformVertical(stack, Vector3f.ZERO, dest);
        VFXHelper.renderStraightArcs(stack, buffer, getLightColor(packedLightIn), 2, 0.03F * (easeCos * 1.5F - 0.5F), 0.015F,
                VFXHelper.getSeedWithTime(seed, age), (int) (easeCos * 255) | 0xFFFF00, (int) (easeCos * 0xA4) | 0xFFFC5200, Math.min(age * 0.3333F * 1.25F - 1.25F, taper));
    }

    @Override
    public int getLightColor(float pTicks) {

        return 0x00F000F0;
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double sx, double sy, double sz, double ex, double ey, double ez) {

            return new ArcParticle(level, sx, sy, sz, ex, ey, ez);
        }

    }

}
