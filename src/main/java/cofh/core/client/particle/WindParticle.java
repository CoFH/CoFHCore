package cofh.core.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class WindParticle extends SpriteTexturedParticle {

    private WindParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 30 + random.nextInt(10);

        scale(2.0F);
        xd = xSpeedIn;
        yd = ySpeedIn;
        zd = zSpeedIn;

        float bScale = 1.0F - (random.nextFloat() * 0.15F);
        float rgScale = bScale - (random.nextFloat() * 0.15F);
        this.rCol = rgScale;
        this.gCol = rgScale;
        this.bCol = bScale;
    }

    @Override
    public void tick() {

        super.tick();
        alpha *= .95F;
    }

    @Override
    public IParticleRenderType getRenderType() {

        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {

            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType data, ClientWorld world, double x, double y, double z, double dx, double dy, double dz) {

            WindParticle particle = new WindParticle(world, x, y, z, dx, dy, dz);
            particle.pickSprite(spriteSet);
            particle.setAlpha(world.random.nextFloat() * 0.3F + 0.6F);
            return particle;
        }
    }

}
