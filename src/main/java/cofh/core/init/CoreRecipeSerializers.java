package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import cofh.lib.util.flags.TagExistsRecipeCondition;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.lib.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SpecialRecipeSerializer<>(SecureRecipe::new));

        CraftingHelper.register(TagExistsRecipeCondition.Serializer.INSTANCE);
    }

}
