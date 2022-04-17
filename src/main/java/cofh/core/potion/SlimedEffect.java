package cofh.core.potion;

import cofh.lib.potion.CustomParticleEffect;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectType;

public class SlimedEffect extends CustomParticleEffect {

    public SlimedEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public IParticleData getParticle() {

        return ParticleTypes.ITEM_SLIME;
    }

}
