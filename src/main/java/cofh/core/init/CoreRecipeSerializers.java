package cofh.core.init;

import cofh.core.util.crafting.SecureRecipe;
import cofh.core.util.crafting.ShapedPotionNBTRecipe;
import cofh.lib.util.flags.TagExistsRecipeCondition;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_POTION;
import static cofh.core.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class CoreRecipeSerializers {

    private CoreRecipeSerializers() {

    }

    public static void register() {

        CraftingHelper.register(TagExistsRecipeCondition.Serializer.INSTANCE);
    }

    public static final RegistryObject<RecipeSerializer<ShapedPotionNBTRecipe>> SHAPED_POTION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_POTION, ShapedPotionNBTRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<SecureRecipe>> SECURE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ID_CRAFTING_SECURABLE, () -> new SimpleCraftingRecipeSerializer<>(SecureRecipe::new));

}
