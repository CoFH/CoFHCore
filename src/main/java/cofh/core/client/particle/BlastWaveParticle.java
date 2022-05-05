package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.SplittableRandom;

@OnlyIn (Dist.CLIENT)
public class BlastWaveParticle extends LevelMatrixStackParticle {

    protected float fLifetime;
    protected int seed;

    private BlastWaveParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double width, double heightScale) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, width, speed, heightScale);
        this.fLifetime = (float) (width / speed);
        this.lifetime = MathHelper.ceil(fLifetime);
        this.setSize((float) width, (float) heightScale);

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLightIn, float partialTicks) {

        SplittableRandom rand = new SplittableRandom(seed);
        float time = age + partialTicks;
        float progress = time / fLifetime;
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float easeCub = MathHelper.easeOutCubic(progress);

        // TODO Hekera FIX
        // VFXHelper.renderCyclone(stack, buffer, getLightColor(partialTicks), bbWidth * 0.5F * easeSin, bbHeight * easeSin, 2, 0.2F * easeCub, time * 0.05F + (float) rand.nextDouble(69F), 0.5F * MathHelper.easePlateau(progress));
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory(SpriteSet sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double speed, double radius, double heightScale) {

            return new BlastWaveParticle(level, x, y, z, speed, radius, heightScale);
        }

    }

}
