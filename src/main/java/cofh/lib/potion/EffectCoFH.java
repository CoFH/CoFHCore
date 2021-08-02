package cofh.lib.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

/**
 * Why does this exist? I'm glad you asked.
 * <p>
 * One word: Protected. As in the constructor. Because of course it is.
 *
 * @author King Lemming
 */
public class EffectCoFH extends Effect {

    public EffectCoFH(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {

        return duration > 0;
    }

}
