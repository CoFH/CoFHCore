package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import cofh.core.util.crafting.ShapedPotionNBTRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_POTION;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        // CraftingHelper.register(TagExistsCondition.Serializer.INSTANCE);
    }

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedPotionNBTRecipe>> SHAPED_POTION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_POTION, ShapedPotionNBTRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SecureRecipe>> SECURE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SimpleCraftingRecipeSerializer<>(SecureRecipe::new));

}
