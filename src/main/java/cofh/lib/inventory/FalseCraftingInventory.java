package cofh.lib.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class FalseCraftingInventory extends CraftingContainer {

    public FalseCraftingInventory(int width, int height) {

        super(FALSE_CONTAINER, width, height);
    }

    private static final AbstractContainerMenu FALSE_CONTAINER = new AbstractContainerMenu(null, -1) {

        @Override
        public ItemStack quickMoveStack(Player player, int index) {

            return ItemStack.EMPTY;
        }

        @Override
        public void slotsChanged(Container inventory) {

        }

        @Override
        public boolean stillValid(Player player) {

            return false;
        }
    };

}
