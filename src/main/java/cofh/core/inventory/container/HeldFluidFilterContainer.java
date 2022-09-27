package cofh.core.inventory.container;

import cofh.core.network.packet.client.ContainerGuiPacket;
import cofh.core.network.packet.server.ContainerConfigPacket;
import cofh.core.util.filter.AbstractFluidFilter;
import cofh.core.util.filter.IFilterOptions;
import cofh.core.util.filter.IFilterableItem;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.inventory.container.slot.SlotLocked;
import cofh.lib.inventory.wrapper.InvWrapperFluids;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.util.helpers.FilterHelper.hasFilter;
import static cofh.core.util.references.CoreReferences.HELD_FLUID_FILTER_CONTAINER;

public class HeldFluidFilterContainer extends ContainerCoFH implements IFilterOptions {

    protected final IFilterableItem filterable;
    protected AbstractFluidFilter filter;
    protected InvWrapperFluids filterInventory;
    protected ItemStack filterStack;

    public SlotLocked lockedSlot;

    public HeldFluidFilterContainer(int windowId, Inventory inventory, Player player) {

        super(HELD_FLUID_FILTER_CONTAINER, windowId, inventory, player);

        allowSwap = false;

        filterStack = hasFilter(player.getMainHandItem()) ? player.getMainHandItem() : player.getOffhandItem();
        filterable = (IFilterableItem) filterStack.getItem();
        filter = (AbstractFluidFilter) filterable.getFilter(filterStack);

        int slots = filter.size();
        filterInventory = new InvWrapperFluids(this, filter.getFluids(), slots);

        int rows = MathHelper.clamp(slots / 3, 1, 3);
        int rowSize = slots / rows;

        int xOffset = 62 - 9 * rowSize;
        int yOffset = 44 - 9 * rows;

        for (int i = 0; i < filter.size(); ++i) {
            addSlot(new SlotFalseCopy(filterInventory, i, xOffset + i % rowSize * 18, yOffset + i / rowSize * 18));
        }
        bindPlayerInventory(inventory);
    }

    @Override
    protected void bindPlayerInventory(Inventory inventory) {

        int xOffset = getPlayerInventoryHorizontalOffset();
        int yOffset = getPlayerInventoryVerticalOffset();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            if (i == inventory.selected) {
                lockedSlot = new SlotLocked(inventory, i, xOffset + i * 18, yOffset + 58);
                addSlot(lockedSlot);
            } else {
                addSlot(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
            }
        }
    }

    public int getFilterSize() {

        return filter.size();
    }

    public List<FluidStack> getFilterStacks() {

        return filterInventory.getStacks();
    }

    @Override
    protected int getMergeableSlotCount() {

        return filterInventory.getContainerSize();
    }

    @Override
    public boolean stillValid(Player playerIn) {

        return lockedSlot.getItem() == filterStack;
    }

    @Override
    public void broadcastChanges() {

        super.broadcastChanges();
        ContainerGuiPacket.sendToClient(this, player);
    }

    @Override
    public void removed(Player playerIn) {

        filter.setFluids(filterInventory.getStacks());
        filter.write(filterStack.getOrCreateTag());
        filterable.onFilterChanged(filterStack);
        super.removed(playerIn);
    }

    // region NETWORK
    @Override
    public FriendlyByteBuf getConfigPacket(FriendlyByteBuf buffer) {

        buffer.writeBoolean(getAllowList());
        buffer.writeBoolean(getCheckNBT());

        return buffer;
    }

    @Override
    public void handleConfigPacket(FriendlyByteBuf buffer) {

        filter.setAllowList(buffer.readBoolean());
        filter.setCheckNBT(buffer.readBoolean());
    }

    @Override
    public FriendlyByteBuf getGuiPacket(FriendlyByteBuf buffer) {

        byte size = (byte) filter.getFluids().size();
        buffer.writeByte(size);
        for (int i = 0; i < size; ++i) {
            buffer.writeFluidStack(filter.getFluids().get(i));
        }
        return buffer;
    }

    @Override
    public void handleGuiPacket(FriendlyByteBuf buffer) {

        byte size = buffer.readByte();
        List<FluidStack> fluidStacks = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            fluidStacks.add(buffer.readFluidStack());
        }
        filterInventory.readFromSource(fluidStacks);
    }
    // endregion

    // region IFilterOptions
    @Override
    public boolean getAllowList() {

        return filter.getAllowList();
    }

    @Override
    public boolean setAllowList(boolean allowList) {

        boolean ret = filter.setAllowList(allowList);
        ContainerConfigPacket.sendToServer(this);
        return ret;
    }

    @Override
    public boolean getCheckNBT() {

        return filter.getCheckNBT();
    }

    @Override
    public boolean setCheckNBT(boolean checkNBT) {

        boolean ret = filter.setCheckNBT(checkNBT);
        ContainerConfigPacket.sendToServer(this);
        return ret;
    }
    // endregion
}
