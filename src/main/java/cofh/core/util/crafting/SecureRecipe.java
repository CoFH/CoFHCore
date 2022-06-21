package cofh.core.util.crafting;

import cofh.lib.api.control.ISecurable.AccessMode;
import cofh.lib.tags.ItemTagsCoFH;
import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import static cofh.core.init.CoreRecipeSerializers.SECURE_RECIPE_SERIALIZER;

public class SecureRecipe extends CustomRecipe {

    public SecureRecipe(ResourceLocation idIn) {

        super(idIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {

        Ingredient ingredientSecure = Ingredient.of(ItemTagsCoFH.LOCKS);
        Ingredient ingredientSecurable = Ingredient.of(ItemTagsCoFH.SECURABLE);

        // boolean flag
        boolean lockItem = false;
        boolean securableItem = false;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (ingredientSecure.test(stack)) {
                    lockItem = true;
                } else if (ingredientSecurable.test(stack) && !SecurityHelper.hasSecurity(stack)) {
                    securableItem = true;
                }
            }
        }
        return lockItem && securableItem;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {

        Ingredient ingredientSecurable = Ingredient.of(ItemTagsCoFH.SECURABLE);

        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                if (ingredientSecurable.test(stack)) {
                    result = stack.copy();
                    break;
                }
            }
        }
        if (!result.isEmpty()) {
            SecurityHelper.createSecurityTag(result);
            SecurityHelper.setAccess(result, AccessMode.PUBLIC);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {

        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return SECURE_RECIPE_SERIALIZER.get();
    }

}
