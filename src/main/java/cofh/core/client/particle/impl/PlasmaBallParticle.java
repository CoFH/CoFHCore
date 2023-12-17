package cofh.core.client.particle.impl;

import cofh.core.common.TransientLightManager;
import cofh.core.common.config.CoreClientConfig;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nonnull;

public class PlasmaBallParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    private PlasmaBallParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, SpriteSet spriteSet) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.spriteSet = spriteSet;
        setSpriteFromAge(spriteSet);
        lifetime = 10;
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
        if (CoreClientConfig.particleDynamicLighting.get()) {
            TransientLightManager.addLight(BlockPos.asLong(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)), 8);
        }
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float pTicks) {

        return 0x00F000F0;
    }

    @Override
    public void setSpriteFromAge(SpriteSet sprite) {

        if (this.age == 0 || this.age == this.lifetime - 1) {
            this.setSprite(sprite.get(0, this.lifetime));
        } else {
            this.setSprite(sprite.get(this.random.nextInt(4) + 1, 4));
        }
    }

    @Nonnull
    public static ParticleProvider<SimpleParticleType> factory(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> new PlasmaBallParticle(level, x, y, z, dx, dy, dz, spriteSet);
    }

}
