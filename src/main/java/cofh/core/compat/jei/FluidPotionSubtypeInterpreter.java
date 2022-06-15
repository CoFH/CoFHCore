package cofh.core.compat.jei;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class FluidPotionSubtypeInterpreter implements IIngredientSubtypeInterpreter<FluidStack> {

    public static final FluidPotionSubtypeInterpreter INSTANCE = new FluidPotionSubtypeInterpreter();

    private FluidPotionSubtypeInterpreter() {

    }

    @Override
    public String apply(FluidStack ingredient, UidContext context) {

        if (!ingredient.hasTag()) {
            return IIngredientSubtypeInterpreter.NONE;
        }
        CompoundTag tag = ingredient.getOrCreateTag();
        Potion potionType = PotionUtils.getPotion(tag);
        String potionTypeString = potionType.getName("");

        StringBuilder stringBuilder = new StringBuilder(potionTypeString);
        List<MobEffectInstance> effects = PotionUtils.getCustomEffects(tag);

        for (MobEffectInstance effect : potionType.getEffects()) {
            stringBuilder.append(";").append(effect);
        }
        for (MobEffectInstance effect : effects) {
            stringBuilder.append(";").append(effect);
        }
        return stringBuilder.toString();
    }

}
