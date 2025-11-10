package cofh.lib.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FalseCraftingContainer implements CraftingContainer {

    private final NonNullList<ItemStack> items;
    private final int width;
    private final int height;

    public FalseCraftingContainer(int width, int height) {

        this.width = width;
        this.height = height;
        this.items = NonNullList.withSize(width * height, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {

        return this.items.size();
    }

    @Override
    public boolean isEmpty() {

        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {

        return pSlot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(pSlot);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {

        return ContainerHelper.takeItem(this.items, pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {

        return ContainerHelper.removeItem(this.items, pSlot, pAmount);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {

        this.items.set(pSlot, pStack);
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player pPlayer) {

        return true;
    }

    @Override
    public void clearContent() {

        this.items.clear();
    }

    @Override
    public int getHeight() {

        return this.height;
    }

    @Override
    public int getWidth() {

        return this.width;
    }

    @Override
    public List<ItemStack> getItems() {

        return List.copyOf(this.items);
    }

    public void fillStackedContents(StackedContents pContents) {

        for (ItemStack itemstack : this.items) {
            pContents.accountSimpleStack(itemstack);
        }
    }

}
