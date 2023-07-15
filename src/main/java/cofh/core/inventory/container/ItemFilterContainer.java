//package cofh.core.inventory.container;
//
//import cofh.core.network.packet.server.ContainerConfigPacket;
//import cofh.core.util.filter.ItemFilter;
//import cofh.core.util.filter.IFilterOptions;
//import cofh.core.util.filter.IFilterableItem;
//import cofh.core.util.filter.IFilterableTile;
//import cofh.core.util.helpers.FilterHelper;
//import cofh.lib.inventory.container.slot.SlotFalseCopy;
//import cofh.lib.inventory.container.slot.SlotLocked;
//import cofh.lib.inventory.wrapper.InvWrapperGeneric;
//import cofh.lib.util.helpers.MathHelper;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//import static cofh.core.init.CoreContainers.ITEM_FILTER_CONTAINER;
//import static cofh.core.util.helpers.FilterHelper.hasFilter;
//
//public class ItemFilterContainer extends ContainerCoFH implements IFilterOptions {
//
//    protected BlockEntity tile;
//    protected IFilterableTile filterableTile;
//
//    protected ItemStack filterStack;
//    protected IFilterableItem filterableItem;
//    public SlotLocked lockedSlot;
//
//    protected ItemFilter filter;
//    protected InvWrapperGeneric filterInventory;
//
//    public final boolean held;
//
//    public ItemFilterContainer(int windowId, Level world, Inventory inventory, Player player, boolean held, BlockPos pos) {
//
//        super(ITEM_FILTER_CONTAINER.get(), windowId, inventory, player);
//
//        this.held = held;
//
//        if (held) {
//            filterStack = hasFilter(player.getMainHandItem()) ? player.getMainHandItem() : player.getOffhandItem();
//            filterableItem = (IFilterableItem) filterStack.getItem();
//            filter = (ItemFilter) filterableItem.getFilter(filterStack);
//        } else {
//            tile = world.getBlockEntity(pos);
//            filterableTile = (IFilterableTile) tile;
//            filter = (ItemFilter) filterableTile.getFilter();
//        }
//        allowSwap = false;
//
//        int slots = filter.size();
//        filterInventory = new InvWrapperGeneric(this, filter.getItems(), slots) {
//            @Override
//            public void setChanged() {
//
//                filter.setItems(filterInventory.getStacks());
//            }
//        };
//
//        int rows = MathHelper.clamp(slots / 3, 1, 3);
//        int rowSize = slots / rows;
//
//        int xOffset = 62 - 9 * rowSize;
//        int yOffset = 44 - 9 * rows;
//
//        for (int i = 0; i < filter.size(); ++i) {
//            addSlot(new SlotFalseCopy(filterInventory, i, xOffset + i % rowSize * 18, yOffset + i / rowSize * 18));
//        }
//        bindPlayerInventory(inventory);
//    }
//
//    @Override
//    protected void bindPlayerInventory(Inventory inventory) {
//
//        if (held) {
//            int xOffset = getPlayerInventoryHorizontalOffset();
//            int yOffset = getPlayerInventoryVerticalOffset();
//
//            for (int i = 0; i < 3; ++i) {
//                for (int j = 0; j < 9; ++j) {
//                    addSlot(new Slot(inventory, j + i * 9 + 9, xOffset + j * 18, yOffset + i * 18));
//                }
//            }
//            for (int i = 0; i < 9; ++i) {
//                if (i == inventory.selected) {
//                    lockedSlot = new SlotLocked(inventory, i, xOffset + i * 18, yOffset + 58);
//                    addSlot(lockedSlot);
//                } else {
//                    addSlot(new Slot(inventory, i, xOffset + i * 18, yOffset + 58));
//                }
//            }
//        } else {
//            super.bindPlayerInventory(inventory);
//        }
//    }
//
//    public IFilterableTile getFilterableTile() {
//
//        return filterableTile;
//    }
//
//    public int getFilterSize() {
//
//        return filter.size();
//    }
//
//    @Override
//    protected int getMergeableSlotCount() {
//
//        return filterInventory.getContainerSize();
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//
//        if (held) {
//            return lockedSlot.getItem() == filterStack;
//        }
//        if (!FilterHelper.hasFilter(filterableTile)) {
//            return false;
//        }
//        return tile != null && !tile.isRemoved() && tile.getBlockPos().distToCenterSqr(player.position()) <= 64D;
//    }
//
//    @Override
//    public void removed(Player playerIn) {
//
//        filter.setItems(filterInventory.getStacks());
//
//        if (held) {
//            filter.write(filterStack.getOrCreateTag());
//            filterableItem.onFilterChanged(filterStack);
//        } else {
//            filterableTile.onFilterChanged();
//        }
//        super.removed(playerIn);
//    }
//
//    // region NETWORK
//    @Override
//    public FriendlyByteBuf getConfigPacket(FriendlyByteBuf buffer) {
//
//        buffer.writeBoolean(getAllowList());
//        buffer.writeBoolean(getCheckNBT());
//
//        return buffer;
//    }
//
//    @Override
//    public void handleConfigPacket(FriendlyByteBuf buffer) {
//
//        filter.setAllowList(buffer.readBoolean());
//        filter.setCheckNBT(buffer.readBoolean());
//    }
//    // endregion
//
//    // region IFilterOptions
//    @Override
//    public boolean getAllowList() {
//
//        return filter.getAllowList();
//    }
//
//    @Override
//    public boolean setAllowList(boolean allowList) {
//
//        boolean ret = filter.setAllowList(allowList);
//        ContainerConfigPacket.sendToServer(this);
//        return ret;
//    }
//
//    @Override
//    public boolean getCheckNBT() {
//
//        return filter.getCheckNBT();
//    }
//
//    @Override
//    public boolean setCheckNBT(boolean checkNBT) {
//
//        boolean ret = filter.setCheckNBT(checkNBT);
//        ContainerConfigPacket.sendToServer(this);
//        return ret;
//    }
//    // endregion
//}
