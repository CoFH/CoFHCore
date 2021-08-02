package cofh.lib.util.recipes;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SerializableRecipeType<T extends SerializableRecipe> implements IRecipeType<T> {

    private final ResourceLocation registryName;

    public SerializableRecipeType(ResourceLocation location) {

        this.registryName = location;
    }

    @Override
    public String toString() {

        return registryName.toString();
    }

    public void register() {

        Registry.register(Registry.RECIPE_TYPE, registryName, this);
    }

}
