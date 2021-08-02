package cofh.lib.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;

public class FalseCraftingInventory extends CraftingInventory {

    public FalseCraftingInventory(int width, int height) {

        super(FALSE_CONTAINER, width, height);
    }

    private static final Container FALSE_CONTAINER = new Container(null, -1) {

        @Override
        public void onCraftMatrixChanged(IInventory inventory) {

        }

        @Override
        public boolean canInteractWith(PlayerEntity player) {

            return false;
        }
    };

}
