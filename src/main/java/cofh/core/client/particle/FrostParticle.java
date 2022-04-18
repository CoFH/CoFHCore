package cofh.core.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class FrostParticle extends SpriteTexturedParticle {

    private FrostParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 40 + random.nextInt(20);

        xd = xSpeedIn;
        yd = ySpeedIn;
        zd = zSpeedIn;
        this.gravity = random.nextFloat() * 0.02F + 0.01F;

        bCol = 1.0F - (random.nextFloat() * 0.15F);
        rCol = gCol = bCol - (random.nextFloat() * 0.15F);
    }

    @Override
    public void tick() {

        super.tick();
        alpha *= .975F;
    }

    @Override
    public IParticleRenderType getRenderType() {

        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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

            FrostParticle particle = new FrostParticle(world, x, y, z, dx, dy, dz);
            particle.pickSprite(spriteSet);
            particle.setAlpha(world.random.nextFloat() * 0.2F + 0.7F);
            return particle;
        }

    }

}
