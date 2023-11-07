package cofh.lib.util.recipes;

import cofh.lib.common.inventory.FalseIInventory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * This class really just serves as a way to ride on Mojang's automated recipe syncing and datapack functionality.
 * It's part of a shim layer, nothing more.
 */
public abstract class SerializableRecipe implements Recipe<FalseIInventory> {

    protected final ResourceLocation recipeId;

    protected SerializableRecipe(ResourceLocation recipeId) {

        this.recipeId = recipeId;
    }

    // region IRecipe
    @Override
    public boolean matches(FalseIInventory inv, Level worldIn) {

        return true;
    }

    @Override
    public ItemStack assemble(FalseIInventory inv, RegistryAccess pRegistryAccess) {

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {

        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {

        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {

        return true;
    }

    @Override
    public ResourceLocation getId() {

        return recipeId;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public abstract RecipeType<?> getType();
    // endregion
}
