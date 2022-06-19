package cofh.core.effect;

import cofh.lib.effect.CustomParticleEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectCategory;

import static cofh.core.init.CoreParticles.SPARK;

public class ShockedEffect extends CustomParticleEffect {

    public ShockedEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return (SimpleParticleType) SPARK.get();
    }

}
