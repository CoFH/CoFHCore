package cofh.core.common.inventory;

import cofh.core.util.helpers.InventoryHelper;
import cofh.lib.common.inventory.SlotCoFH;
import cofh.lib.common.inventory.SlotFalseCopy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerCoFH extends AbstractContainerMenu {

    protected boolean allowSwap = true;
    protected boolean falseSlotSupport = true;
    protected boolean syncing = false;

    protected Player player;

    protected List<SlotCoFH> augmentSlots = new ArrayList<>();

    public ContainerCoFH(@Nullable MenuType<?> type, int id, Inventory inventory, Player player) {

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

    protected void bindAugmentSlots(Container inventory, int startIndex, int numSlots) {

        for (int i = 0; i < numSlots; ++i) {
            SlotCoFH slot = new SlotCoFH(inventory, i + startIndex, 0, 0, 1);
            augmentSlots.add(slot);
            addSlot(slot);
        }
        ((ArrayList<SlotCoFH>) augmentSlots).trimToSize();
    }

    protected void bindPlayerInventory(Inventory inventory) {

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

    protected boolean supportsShiftClick(Player player, int index) {

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
    public FriendlyByteBuf getConfigPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    public void handleConfigPacket(FriendlyByteBuf buffer) {

    }

    public FriendlyByteBuf getGuiPacket(FriendlyByteBuf buffer) {

        return buffer;
    }

    public void handleGuiPacket(FriendlyByteBuf buffer) {

    }
    // endregion

    // region OVERRIDES
    @Override
    public ItemStack quickMoveStack(Player player, int index) {

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
    public void clicked(int index, int dragType, ClickType clickTypeIn, Player player) {

        if (clickTypeIn == ClickType.SWAP && !allowSwap) {
            return;
        }
        if (falseSlotSupport) {
            Slot slot = index < 0 ? null : slots.get(index);
            if (slot instanceof SlotFalseCopy) {
                if (dragType == 2) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.set(this.getCarried().isEmpty() ? ItemStack.EMPTY : this.getCarried().copy());
                }
                return;
            }
        }
        super.clicked(index, dragType, clickTypeIn, player);
    }

    @Override
    public void initializeContents(int p_182411_, List<ItemStack> p_182412_, ItemStack p_182413_) {

        syncing = true;
        super.initializeContents(p_182411_, p_182412_, p_182413_);
        syncing = false;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {

        return InventoryHelper.mergeItemStack(slots, stack, startIndex, endIndex, reverseDirection);
    }
    // endregion
}
