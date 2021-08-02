package cofh.core.data;

import cofh.core.init.CoreFlags;
import cofh.lib.data.RecipeProviderCoFH;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static cofh.core.CoFHCore.ITEMS;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreIDs.ID_ECTOPLASM;

public class CoreRecipeProvider extends RecipeProviderCoFH {

    public CoreRecipeProvider(DataGenerator generatorIn) {

        super(generatorIn, ID_COFH_CORE);
        manager = CoreFlags.manager();
    }

    @Override
    public String getName() {

        return "CoFH Core: Recipes";
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

        ShapelessRecipeBuilder.shapelessRecipe(ITEMS.get(ID_ECTOPLASM))
                .addIngredient(Items.GHAST_TEAR)
                .addIngredient(Tags.Items.SLIMEBALLS)
                .addCriterion("has_ghast_tear", hasItem(Items.GHAST_TEAR))
                .build(withConditions(consumer).flag(ID_ECTOPLASM));
    }

}
