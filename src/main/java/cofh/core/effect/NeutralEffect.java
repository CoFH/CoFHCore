package cofh.core.effect;

import cofh.lib.effect.EffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class NeutralEffect extends EffectCoFH {

    public NeutralEffect(MobEffectCategory typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return false;
    }

    @Override
    public List<ItemStack> getCurativeItems() {

        return Collections.emptyList();
    }

}
