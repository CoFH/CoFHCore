//package cofh.core.inventory.container;
//
//import cofh.core.network.packet.server.ContainerPacket;
//import cofh.core.util.filter.AbstractFluidFilter;
//import cofh.lib.inventory.container.slot.SlotFalseCopy;
//import cofh.lib.inventory.wrapper.InvWrapperGeneric;
//import cofh.lib.util.filter.IFilterOptions;
//import cofh.lib.util.filter.IFilterableTile;
//import cofh.lib.util.helpers.FilterHelper;
//import cofh.lib.util.helpers.MathHelper;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import static cofh.lib.util.references.CoreReferences.TILE_FLUID_FILTER_CONTAINER;
//
//public class TileFluidFilterContainer extends TileContainer implements IFilterOptions {
//
//    protected final IFilterableTile filterable;
//    protected AbstractFluidFilter filter;
//    protected InvWrapperGeneric filterInventory;
//
//    public TileFluidFilterContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory, PlayerEntity player) {
//
//        super(TILE_FLUID_FILTER_CONTAINER, windowId, world, pos, inventory, player);
//
//        allowSwap = false;
//
//        filterable = (IFilterableTile) world.getTileEntity(pos);
//        filter = (AbstractFluidFilter) filterable.getFilter();
//
//        int slots = filter.size();
//        filterInventory = new InvWrapperGeneric(this, filter.getFluids(), slots);
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
//    public IFilterableTile getFilterableTile() {
//
//        return filterable;
//    }
//
//    public int getFilterSize() {
//
//        return filter.size();
//    }
//
//    @Override
//    protected int getSizeInventory() {
//
//        return filterInventory.getSizeInventory();
//    }
//
//    @Override
//    public boolean canInteractWith(PlayerEntity player) {
//
//        if (!FilterHelper.hasFilter(filterable)) {
//            return false;
//        }
//        return super.canInteractWith(player);
//    }
//
//    @Override
//    public void onContainerClosed(PlayerEntity playerIn) {
//
//        filter.setFluids(filterInventory.getStacks());
//        filterable.onFilterChanged();
//        super.onContainerClosed(playerIn);
//    }
//
//    // region NETWORK
//    @Override
//    public PacketBuffer getContainerPacket(PacketBuffer buffer) {
//
//        buffer.writeBoolean(getAllowList());
//        buffer.writeBoolean(getCheckNBT());
//
//        return buffer;
//    }
//
//    @Override
//    public void handleContainerPacket(PacketBuffer buffer) {
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
//        ContainerPacket.sendToServer(this);
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
//        ContainerPacket.sendToServer(this);
//        return ret;
//    }
//    // endregion
//}
