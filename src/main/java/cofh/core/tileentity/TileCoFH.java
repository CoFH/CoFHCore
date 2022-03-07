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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class TileCoFH extends BlockEntity implements ITileCallback, ITilePacketHandler, ITileXpHandler, IConveyableData {

    protected int numPlayersUsing;

    public TileCoFH(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {

        super(tileEntityTypeIn, pos, state);
    }

    @Override
    public void onLoad() {

        super.onLoad();

        if (level != null && Utils.isClientWorld(level)) {
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

    public void sendGuiNetworkData(AbstractContainerMenu container, ContainerListener player) {

        if (hasGuiPacket() && player instanceof ServerPlayer && (!(player instanceof FakePlayer))) {
            TileGuiPacket.sendToClient(this, (ServerPlayer) player);
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
    public boolean onActivatedDelegate(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult result) {

        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(handler -> FluidHelper.interactWithHandler(player.getItemInHand(hand), handler, player, hand)).orElse(false);
    }

    public boolean hasGuiPacket() {

        return true;
    }

    public void animateTick(BlockState state, Level worldIn, BlockPos pos, Random rand) {

    }

    protected Object getSound() {

        return null;
    }

    protected void markDirtyFast() {

        if (this.level != null) {
            this.level.blockEntityChanged(this.worldPosition);
        }
    }
    // endregion

    // region GUI
    public boolean playerWithinDistance(Player player, double distanceSq) {

        return !isRemoved() && worldPosition.distToCenterSqr(player.position()) <= distanceSq;
    }
    // endregion

    // region NETWORK
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {

        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {

        load(pkt.getTag());
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
    public Level world() {

        return level;
    }
    // endregion
}
