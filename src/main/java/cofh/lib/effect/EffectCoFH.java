package cofh.lib.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Why does this exist? I'm glad you asked.
 * <p>
 * One word: Protected. As in the constructor. Because of course it is.
 *
 * @author King Lemming
 */
public class EffectCoFH extends MobEffect {

    public EffectCoFH(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return duration > 0;
    }

}
