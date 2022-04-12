package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.VFXHelper;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.SplittableRandom;

@OnlyIn(Dist.CLIENT)
public class BlastWaveParticle extends LevelMatrixStackParticle {

    protected float fLifetime;
    protected int seed;

    private BlastWaveParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double speed, double width, double heightScale) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, width, speed, heightScale);
        this.fLifetime = (float) (width / speed);
        this.lifetime = MathHelper.ceil(fLifetime);
        this.setSize((float) width, (float) heightScale);

        hasPhysics = false;
        xd = yd = zd = 0;
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn, float partialTicks) {

        SplittableRandom rand = new SplittableRandom(seed);
        float time = age + partialTicks;
        float progress = time / fLifetime;
        float easeSin = MathHelper.sin(progress * MathHelper.F_PI * 0.5F);
        float easeCub = MathHelper.easeOutCubic(progress);
        VFXHelper.renderCyclone(stack, buffer, getLightColor(partialTicks), bbWidth * 0.5F * easeSin, bbHeight * easeSin, 2, 0.2F * easeCub, time * 0.05F + (float) rand.nextDouble(69F), 0.5F * MathHelper.easePlateau(progress));
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        public Factory(IAnimatedSprite sprite) {

        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double speed, double radius, double heightScale) {

            return new BlastWaveParticle(world, x, y, z, speed, radius, heightScale);
        }
    }

}
