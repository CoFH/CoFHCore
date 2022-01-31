package cofh.core.client.particle;

import cofh.lib.entity.ElectricArcEntity;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class PlasmaBallParticle extends SpriteTexturedParticle {

    private final IAnimatedSprite spriteSet;

    private PlasmaBallParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IAnimatedSprite spriteSet) {

        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.spriteSet = spriteSet;
        setSpriteFromAge(spriteSet);
        lifetime = ElectricArcEntity.duration;
        scale(3.0F);

        xd = xSpeedIn;
        yd = ySpeedIn;
        zd = zSpeedIn;

        oRoll = roll = random.nextFloat() * 2 * (float) Math.PI;
    }

    @Override
    public void tick() {

        oRoll = roll = random.nextFloat() * 2 * (float) Math.PI;
        setSpriteFromAge(spriteSet);
        super.tick();
    }

    @Override
    public IParticleRenderType getRenderType() {

        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float pTicks){

        return 15728880;
    }

    @Override
    public void setSpriteFromAge(IAnimatedSprite sprite) {

        if (this.age == 0 || this.age == this.lifetime - 1) {
            this.setSprite(sprite.get(0, this.lifetime));
        } else {
            this.setSprite(sprite.get(this.random.nextInt(4) + 1, 4));
        }
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

            PlasmaBallParticle particle = new PlasmaBallParticle(world, x, y, z, dx, dy, dz, spriteSet);
            return particle;
        }
    }

}
