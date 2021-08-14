package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.lib.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SpecialRecipeSerializer<>(SecureRecipe::new));
    }

}
