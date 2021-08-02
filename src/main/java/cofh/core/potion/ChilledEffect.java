package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.EffectType;

import static cofh.lib.util.constants.Constants.UUID_EFFECT_CHILLED_MOVEMENT_SPEED;

public class ChilledEffect extends EffectCoFH {

    public ChilledEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {

        if (modifier.getID() == UUID_EFFECT_CHILLED_MOVEMENT_SPEED) {
            return Math.max(-0.90D, super.getAttributeModifierAmount(amplifier, modifier));
        }
        return super.getAttributeModifierAmount(amplifier, modifier);
    }

}
