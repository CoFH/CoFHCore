package cofh.core.common.effect;

import cofh.lib.common.effect.MobEffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class NeutralMobEffect extends MobEffectCoFH {

    public NeutralMobEffect(MobEffectCategory typeIn, int liquidColorIn) {

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
