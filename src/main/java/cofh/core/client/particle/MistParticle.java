package cofh.core.client.particle;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@OnlyIn (Dist.CLIENT)
public class MistParticle extends TextureSheetParticle {

    private MistParticle(ClientLevel levelIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {

        super(levelIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        lifetime = 100;

        xd = xSpeedIn;
        yd = ySpeedIn;
        zd = zSpeedIn;

        rCol = gCol = 0.95F - (random.nextFloat() * 0.05F);
        oRoll = roll = random.nextFloat() * MathHelper.F_TAU;
        scale(20);
        setAlpha(level.random.nextFloat() * 1.0F);
    }

    @Override
    public void tick() {

        super.tick();
        oRoll = roll;
        roll += 0.1F;
        alpha *= .975F;
    }

    @Override
    public ParticleRenderType getRenderType() {

        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static ParticleProvider<SimpleParticleType> iceMist(SpriteSet spriteSet) {

        return (data, level, x, y, z, dx, dy, dz) -> {
            MistParticle particle = new MistParticle(level, x, y, z, dx, dy, dz);
            particle.pickSprite(spriteSet);
            //particle.setSize(1.0F, 1.0F);
            //particle.setAlpha(level.random.nextFloat() * 0.2F);
            return particle;
        };
    }

}
