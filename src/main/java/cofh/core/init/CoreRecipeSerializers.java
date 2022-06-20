package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import cofh.core.util.crafting.ShapedPotionNBTRecipe;
import cofh.lib.util.flags.TagExistsRecipeCondition;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_POTION;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        RECIPE_SERIALIZERS.register(ID_CRAFTING_POTION, ShapedPotionNBTRecipe.Serializer::new);
        RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SimpleRecipeSerializer<>(SecureRecipe::new));

        CraftingHelper.register(TagExistsRecipeCondition.Serializer.INSTANCE);
    }

}
