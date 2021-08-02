package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectType;

import java.util.Collections;
import java.util.List;

public class NeutralEffect extends EffectCoFH {

    public NeutralEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {

        return false;
    }

    @Override
    public List<ItemStack> getCurativeItems() {

        return Collections.emptyList();
    }

}
