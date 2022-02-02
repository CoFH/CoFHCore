package cofh.core.potion;

import cofh.lib.potion.CustomParticleEffect;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectType;

public class ShockedEffect extends CustomParticleEffect {

    public ShockedEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public IParticleData getParticle() {

        return CoreReferences.SPARK_PARTICLE;
    }

}
