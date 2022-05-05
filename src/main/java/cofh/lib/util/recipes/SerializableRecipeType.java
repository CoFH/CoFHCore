package cofh.lib.util.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

public class SerializableRecipeType<T extends SerializableRecipe> implements RecipeType<T> {

    private final ResourceLocation registryName;

    public SerializableRecipeType(ResourceLocation location) {

        this.registryName = location;
    }

    @Override
    public String toString() {

        return registryName.toString();
    }

}
