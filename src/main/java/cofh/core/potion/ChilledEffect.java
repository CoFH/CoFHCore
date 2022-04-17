package cofh.core.potion;

import cofh.lib.potion.CustomParticleEffect;
import cofh.lib.util.references.CoreReferences;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectType;

import static cofh.lib.util.constants.Constants.UUID_EFFECT_CHILLED_MOVEMENT_SPEED;

public class ChilledEffect extends CustomParticleEffect {

    public ChilledEffect(EffectType typeIn, int liquidColorIn) {

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
    public IParticleData getParticle() {

        return CoreReferences.FROST_PARTICLE;
    }

}
