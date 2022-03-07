package cofh.core.data;

import cofh.core.init.CoreFlags;
import cofh.lib.data.RecipeProviderCoFH;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
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
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapelessRecipeBuilder.shapeless(ITEMS.get(ID_ECTOPLASM))
                .requires(Items.GHAST_TEAR)
                .requires(Tags.Items.SLIMEBALLS)
                .unlockedBy("has_ghast_tear", has(Items.GHAST_TEAR))
                .save(withConditions(consumer).flag(ID_ECTOPLASM));
    }

}
