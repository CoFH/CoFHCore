package cofh.core.effect;

import cofh.lib.content.effect.CustomParticleMobEffect;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import static cofh.core.init.CoreParticles.FROST;
import static cofh.lib.util.Constants.UUID_EFFECT_CHILLED_MOVEMENT_SPEED;

public class ChilledMobEffect extends CustomParticleMobEffect {

    public ChilledMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {

        if (modifier.getId() == UUID_EFFECT_CHILLED_MOVEMENT_SPEED) {
            return Math.max(-0.90D, super.getAttributeModifierValue(amplifier, modifier));
        }
        return super.getAttributeModifierValue(amplifier, modifier);
    }

    @Override
    public ParticleOptions getParticle() {

        return (SimpleParticleType) FROST.get();
    }

}
