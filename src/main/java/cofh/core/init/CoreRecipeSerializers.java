package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import cofh.core.util.crafting.ShapedPotionNBTRecipe;
import cofh.lib.util.flags.TagExistsCondition;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.registries.RegistryObject;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_POTION;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        CraftingHelper.register(TagExistsCondition.Serializer.INSTANCE);
    }

    public static final RegistryObject<RecipeSerializer<ShapedPotionNBTRecipe>> SHAPED_POTION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_POTION, ShapedPotionNBTRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<SecureRecipe>> SECURE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SimpleCraftingRecipeSerializer<>(SecureRecipe::new));

}
