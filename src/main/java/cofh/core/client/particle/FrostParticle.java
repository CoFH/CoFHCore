package cofh.core.client.particle;

import cofh.core.util.helpers.vfx.RenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class FrostParticle extends TextureSheetParticle {

    private FrostParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
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
    public ParticleRenderType getRenderType() {

        return RenderTypes.PARTICLE_SHEET_TRANSLUCENT_BLEND;
    }

    @OnlyIn (Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {

            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType data, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {

            FrostParticle particle = new FrostParticle(level, x, y, z, dx, dy, dz);
            particle.pickSprite(spriteSet);
            particle.setAlpha(level.random.nextFloat() * 0.2F + 0.7F);
            return particle;
        }

    }

}
