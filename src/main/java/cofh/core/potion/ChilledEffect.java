package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import static cofh.lib.util.constants.Constants.UUID_EFFECT_CHILLED_MOVEMENT_SPEED;

public class ChilledEffect extends EffectCoFH {

    public ChilledEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {

        if (modifier.getId() == UUID_EFFECT_CHILLED_MOVEMENT_SPEED) {
            return Math.max(-0.90D, super.getAttributeModifierValue(amplifier, modifier));
        }
        return super.getAttributeModifierValue(amplifier, modifier);
    }

}
