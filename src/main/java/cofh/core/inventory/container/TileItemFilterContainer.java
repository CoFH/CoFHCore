package cofh.core.inventory.container;

import cofh.core.network.packet.server.ContainerConfigPacket;
import cofh.core.util.filter.AbstractItemFilter;
import cofh.core.util.filter.IFilterOptions;
import cofh.core.util.filter.IFilterableTile;
import cofh.core.util.helpers.FilterHelper;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.inventory.wrapper.InvWrapperGeneric;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static cofh.core.util.references.CoreReferences.TILE_ITEM_FILTER_CONTAINER;

public class TileItemFilterContainer extends TileContainer implements IFilterOptions {

    protected final IFilterableTile filterable;
    protected AbstractItemFilter filter;
    protected InvWrapperGeneric filterInventory;

    public TileItemFilterContainer(int windowId, Level world, BlockPos pos, Inventory inventory, Player player) {

        super(TILE_ITEM_FILTER_CONTAINER, windowId, world, pos, inventory, player);

        allowSwap = false;

        filterable = (IFilterableTile) world.getBlockEntity(pos);
        filter = (AbstractItemFilter) filterable.getFilter(0);

        int slots = filter.size();
        filterInventory = new InvWrapperGeneric(this, filter.getItems(), slots);

        int rows = MathHelper.clamp(slots / 3, 1, 3);
        int rowSize = slots / rows;

        int xOffset = 62 - 9 * rowSize;
        int yOffset = 44 - 9 * rows;

        for (int i = 0; i < filter.size(); ++i) {
            addSlot(new SlotFalseCopy(filterInventory, i, xOffset + i % rowSize * 18, yOffset + i / rowSize * 18));
        }
        bindPlayerInventory(inventory);
    }

    public IFilterableTile getFilterableTile() {

        return filterable;
    }

    public int getFilterSize() {

        return filter.size();
    }

    @Override
    protected int getMergeableSlotCount() {

        return filterInventory.getContainerSize();
    }

    @Override
    public boolean stillValid(Player player) {

        if (!FilterHelper.hasFilter(filterable, 0)) {
            return false;
        }
        return super.stillValid(player);
    }

    @Override
    public void removed(Player playerIn) {

        filter.setItems(filterInventory.getStacks());
        filterable.onFilterChanged(0);
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
