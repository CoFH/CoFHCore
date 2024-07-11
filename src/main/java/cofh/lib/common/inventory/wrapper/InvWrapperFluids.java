package cofh.lib.common.inventory.wrapper;

import cofh.core.util.helpers.FluidHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class InvWrapperFluids implements Container {

    private final NonNullList<FluidStack> stackList;
    private final AbstractContainerMenu eventHandler;

    public InvWrapperFluids(AbstractContainerMenu eventHandler, List<FluidStack> contents, int size) {

        this.stackList = NonNullList.withSize(size, FluidStack.EMPTY);
        this.eventHandler = eventHandler;

        readFromSource(contents);
    }

    public List<FluidStack> getStacks() {

        return stackList;
    }

    public void readFromSource(List<FluidStack> contents) {

        this.stackList.clear();
        for (int i = 0; i < Math.min(contents.size(), getContainerSize()); ++i) {
            this.stackList.set(i, contents.get(i));
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {

        return stack.isEmpty() || !FluidHelper.getFluidContainedInItem(stack).isEmpty();
    }

    // region IInventory
    @Override
    public int getContainerSize() {

        return this.stackList.size();
    }

    public boolean isEmpty() {

        for (FluidStack stack : this.stackList) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        if (index >= 0 && index < getContainerSize()) {
            if (FluidHelper.getFluidContainedInItem(stack).isEmpty()) {
                this.stackList.set(index, FluidStack.EMPTY);
            } else {
                this.stackList.set(index, FluidHelper.getFluidContainedInItem(stack));
            }
            this.eventHandler.slotsChanged(this);
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {

        return true;
    }

    @Override
    public void clearContent() {

        this.stackList.clear();
    }
    // endregion
}
