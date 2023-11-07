package cofh.core.common.effect;

import cofh.lib.common.effect.CustomParticleMobEffect;
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
