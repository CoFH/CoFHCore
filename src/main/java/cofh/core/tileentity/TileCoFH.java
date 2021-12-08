package cofh.core.tileentity;

import cofh.core.network.packet.client.TileGuiPacket;
import cofh.core.util.ProxyUtils;
import cofh.core.util.helpers.FluidHelper;
import cofh.lib.tileentity.IAreaEffectTile;
import cofh.lib.tileentity.ITileCallback;
import cofh.lib.tileentity.ITilePacketHandler;
import cofh.lib.tileentity.ITileXpHandler;
import cofh.lib.util.IConveyableData;
import cofh.lib.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class TileCoFH extends TileEntity implements ITileCallback, ITilePacketHandler, ITileXpHandler, IConveyableData {

    protected int numPlayersUsing;

    public TileCoFH(TileEntityType<?> tileEntityTypeIn) {

        super(tileEntityTypeIn);
    }

    @Override
    public void onLoad() {

        super.onLoad();

        if (level != null && Utils.isClientWorld(level)) {
            if (!hasClientUpdate()) {
                level.tickableBlockEntities.remove(this);
            }
            if (this instanceof IAreaEffectTile) {
                ProxyUtils.addAreaEffectTile((IAreaEffectTile) this);
            }
        }
        clearRemoved();
    }

    @Override
    public void setRemoved() {

        if (this instanceof IAreaEffectTile) {
            ProxyUtils.removeAreaEffectTile((IAreaEffectTile) this);
        }
        super.setRemoved();
    }

    public int getPlayersUsing() {

        return numPlayersUsing;
    }

    public void addPlayerUsing() {

        ++numPlayersUsing;
    }

    public void removePlayerUsing() {

        --numPlayersUsing;
    }

    public void receiveGuiNetworkData(int id, int data) {

    }

    public void sendGuiNetworkData(Container container, IContainerListener player) {

        if (hasGuiPacket() && player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            TileGuiPacket.sendToClient(this, (ServerPlayerEntity) player);
        }
    }

    // region BASE OVERRIDES
    // TODO: Decide if this is necessary/prudent.

    //    @Override
    //    public void read(BlockState state, CompoundNBT nbt) {
    //
    //        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    //    }
    //
    //    @Override
    //    public CompoundNBT write(CompoundNBT compound) {
    //
    //        ResourceLocation resourcelocation = TileEntityType.getId(this.getType());
    //        if (resourcelocation == null) {
    //            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
    //        } else {
    //            compound.putString("id", resourcelocation.toString());
    //            compound.putInt("x", this.pos.getX());
    //            compound.putInt("y", this.pos.getY());
    //            compound.putInt("z", this.pos.getZ());
    //            return compound;
    //        }
    //    }
    //
    //    @Override
    //    public CompoundNBT getTileData() {
    //
    //        return new CompoundNBT();
    //    }
    // endregion

    // region HELPERS
    public TileCoFH worldContext(BlockState state, IBlockReader world) {

        return this;
    }

    public boolean onActivatedDelegate(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(handler -> FluidHelper.interactWithHandler(player.getItemInHand(hand), handler, player, hand)).orElse(false);
    }

    public boolean hasClientUpdate() {

        return false;
    }

    public boolean hasGuiPacket() {

        return true;
    }

    public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand) {

    }

    protected Object getSound() {

        return null;
    }

    protected void markDirtyFast() {

        if (this.level != null) {
            this.level.blockEntityChanged(this.worldPosition, this);
        }
    }
    // endregion

    // region GUI
    public boolean playerWithinDistance(PlayerEntity player, double distanceSq) {

        return !isRemoved() && worldPosition.distSqr(player.position(), true) <= distanceSq;
    }
    // endregion

    // region NETWORK
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {

        return new SUpdateTileEntityPacket(worldPosition, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {

        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {

        load(this.blockState, pkt.getTag());
    }
    // endregion

    // region ITileCallback
    @Override
    public Block block() {

        return getBlockState().getBlock();
    }

    @Override
    public BlockState state() {

        return getBlockState();
    }

    @Override
    public BlockPos pos() {

        return worldPosition;
    }

    @Override
    public World world() {

        return level;
    }
    // endregion
}
