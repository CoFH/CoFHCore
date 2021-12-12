package cofh.lib.inventory.container;

import cofh.lib.inventory.container.slot.SlotCoFH;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.util.helpers.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerCoFH extends Container {

    protected boolean allowSwap = true;
    protected boolean falseSlotSupport = true;
    protected boolean syncing = false;

    protected PlayerEntity player;

    protected List<SlotCoFH> augmentSlots = new ArrayList<>();

    public ContainerCoFH(@Nullable ContainerType<?> type, int id, PlayerInventory inventory, PlayerEntity player) {

        super(type, id);
        this.player = player;
    }

    // region HELPERS
    public final int getNumAugmentSlots() {

        return augmentSlots.size();
    }

    public final List<SlotCoFH> getAugmentSlots() {

        return augmentSlots;
    }

    protected void bindAugmentSlots(IInventory inventory, int startIndex, int numSlots) {

        for (int i = 0; i < numSlots; ++i) {
            SlotCoFH slot = new SlotCoFH(inventory, i + startIndex, 0, 0, 1);
            augmentSlots.add(slot);
            addSlot(slot);
        }
        ((ArrayList<SlotCoFH>) augmentSlots).trimToSize();
    }

    protected void bindPlayerInventory(PlayerInventory inventory) {

        int xOffset = getPlayerInventoryHorizontalOffset();
        int yOffset = getPlayerInventoryVerticalOffset();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
        }
    }

    protected int getPlayerInventoryHorizontalOffset() {

        return 8;
    }

    protected int getPlayerInventoryVerticalOffset() {

        return 84;
    }

    protected abstract int getMergeableSlotCount();

    protected boolean supportsShiftClick(PlayerEntity player, int index) {

        return true;
    }

    protected boolean performMerge(int index, ItemStack stack) {

        // TODO: Consider reverting or allowing augment shift-click in some cases.
        int invBase = getMergeableSlotCount();
        // int invBase = getSizeInventory() - getNumAugmentSlots();
        int invFull = slots.size();
        int invHotbar = invFull - 9;
        int invPlayer = invHotbar - 27;

        if (index < invPlayer) {
            return moveItemStackTo(stack, invPlayer, invFull, false);
        } else {
            return moveItemStackTo(stack, 0, invBase, false);
        }
    }
    // endregion

    // region NETWORK
    public PacketBuffer getContainerPacket(PacketBuffer buffer) {

        return buffer;
    }

    public void handleContainerPacket(PacketBuffer buffer) {

    }
    // endregion

    // region OVERRIDES
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {

        if (!supportsShiftClick(player, index)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            if (!performMerge(index, stackInSlot)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stackInSlot, stack);

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return stack;
    }

    @Override
    public ItemStack clicked(int index, int dragType, ClickType clickTypeIn, PlayerEntity player) {

        if (clickTypeIn == ClickType.SWAP && !allowSwap) {
            return ItemStack.EMPTY;
        }
        if (falseSlotSupport) {
            Slot slot = index < 0 ? null : slots.get(index);
            if (slot instanceof SlotFalseCopy) {
                if (dragType == 2) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.set(player.inventory.getCarried().isEmpty() ? ItemStack.EMPTY : player.inventory.getCarried().copy());
                }
                return player.inventory.getCarried();
            }
        }
        return super.clicked(index, dragType, clickTypeIn, player);
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void setAll(List<ItemStack> stacks) {

        syncing = true;
        for (int i = 0; i < stacks.size(); ++i) {
            setItem(i, stacks.get(i));
        }
        syncing = false;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {

        return InventoryHelper.mergeItemStack(slots, stack, startIndex, endIndex, reverseDirection);
    }
    // endregion
}
