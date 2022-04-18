package cofh.core.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class SparkParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteSet;

    private SparkParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IAnimatedSprite spriteSet) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 8;

        this.spriteSet = spriteSet;
        pickSprite(spriteSet);
        xd = xSpeedIn;
        yd = ySpeedIn;
        zd = zSpeedIn;
        scale(1.5F);
        oRoll = roll = random.nextFloat() * 2 * (float) Math.PI;
    }

    @Override
    public void tick() {

        super.tick();
        if ((age & 1) == 0) {
            pickSprite(spriteSet);
        }
    }

    @Override
    public IParticleRenderType getRenderType() {

        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float pTicks) {

        return 15728880;
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {

            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {

            return new SparkParticle(world, x, y, z, dx, dy, dz, spriteSet);
        }

    }

}
