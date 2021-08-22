package cofh.core.util.crafting;

import cofh.lib.util.control.ISecurable.AccessMode;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.references.ItemTagsCoFH;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import static cofh.core.CoFHCore.RECIPE_SERIALIZERS;
import static cofh.lib.util.references.CoreIDs.ID_CRAFTING_SECURABLE;

public class SecureRecipe extends SpecialRecipe {

    public SecureRecipe(ResourceLocation idIn) {

        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {

        Ingredient ingredientSecure = Ingredient.fromTag(ItemTagsCoFH.LOCKS);
        Ingredient ingredientSecurable = Ingredient.fromTag(ItemTagsCoFH.SECURABLE);

        // boolean flag
        boolean lockItem = false;
        boolean securableItem = false;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
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
    public ItemStack getCraftingResult(CraftingInventory inv) {

        Ingredient ingredientSecurable = Ingredient.fromTag(ItemTagsCoFH.SECURABLE);

        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
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
    public boolean canFit(int width, int height) {

        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {

        return RECIPE_SERIALIZERS.get(ID_CRAFTING_SECURABLE);
    }

}
