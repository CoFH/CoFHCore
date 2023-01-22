package cofh.core.effect;

import cofh.lib.effect.CustomParticleMobEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectCategory;

import static cofh.core.init.CoreParticles.SPARK;

public class ShockedMobEffect extends CustomParticleMobEffect {

    public ShockedMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public ParticleOptions getParticle() {

        return SPARK.get();
    }

}
