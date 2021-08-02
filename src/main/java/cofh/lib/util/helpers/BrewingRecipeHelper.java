package cofh.lib.util.helpers;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.lang.reflect.Field;

public class BrewingRecipeHelper {

    private static final Class c_MixPredicate;
    private static final Field f_from;
    private static final Field f_ingredient;
    private static final Field f_to;

    static {
        try {
            c_MixPredicate = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");

            f_from = ObfuscationReflectionHelper.findField(c_MixPredicate, "field_185198_a");
            f_ingredient = ObfuscationReflectionHelper.findField(c_MixPredicate, "field_185199_b");
            f_to = ObfuscationReflectionHelper.findField(c_MixPredicate, "field_185200_c");
        } catch (Throwable e) {
            throw new RuntimeException("Failed to reflect.", e);
        }
    }

    private static <T> T getField(Field field, Object o) {

        try {
            // noinspection unchecked
            return (T) field.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getFrom(Object o) {

        return getField(f_from, o);
    }

    public static Potion getFromPotion(Object o) {

        return ((IRegistryDelegate<Potion>) getFrom(o)).get();
    }

    public static <T> T getIngredient(Object o) {

        return getField(f_ingredient, o);
    }

    public static <T> T getTo(Object o) {

        return getField(f_to, o);
    }

    public static Potion getToPotion(Object o) {

        return ((IRegistryDelegate<Potion>) getTo(o)).get();
    }

}