package cofh.core.util.crafting;

import cofh.lib.util.crafting.IngredientWithCount;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class CustomIngredients {

    public static void setup() {

        CraftingHelper.register(new ResourceLocation(ID_COFH_CORE, "with_count"), IngredientWithCount.Serializer.INSTANCE);
    }

}
