package cofh.core.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class SparkParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    private SparkParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, SpriteSet spriteSet) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
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
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float pTicks) {

        return 0x00F000F0;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {

            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

            return new SparkParticle(level, x, y, z, dx, dy, dz, spriteSet);
        }

    }

}
