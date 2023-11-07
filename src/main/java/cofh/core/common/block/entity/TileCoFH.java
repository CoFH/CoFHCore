package cofh.core.common.block.entity;

import cofh.core.common.network.packet.client.TileGuiPacket;
import cofh.core.util.ProxyUtils;
import cofh.core.util.helpers.FluidHelper;
import cofh.lib.api.IConveyableData;
import cofh.lib.api.block.entity.IAreaEffectTile;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import cofh.lib.api.block.entity.ITileCallback;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;

public class TileCoFH extends BlockEntity implements ITileCallback, IPacketHandlerTile, ITileXpHandler, IConveyableData {

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

    public void markChunkUnsaved() {

        if (this.level != null) {
            if (this.level.hasChunkAt(this.worldPosition)) {
                this.level.getChunkAt(this.worldPosition).setUnsaved(true);
            }
        }
    }

    public void addPlayerUsing() {

    }

    public void removePlayerUsing() {

    }

    public void receiveGuiNetworkData(int id, int data) {

    }

    public void sendGuiNetworkData(AbstractContainerMenu container, Player player) {

        if (hasGuiPacket() && player instanceof ServerPlayer && (!(player instanceof FakePlayer))) {
            TileGuiPacket.sendToClient(this, (ServerPlayer) player);
        }
    }

    // region HELPERS
    public boolean onActivatedDelegate(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult result) {

        return getCapability(ForgeCapabilities.FLUID_HANDLER).map(handler -> FluidHelper.interactWithHandler(player.getItemInHand(hand), handler, player, hand)).orElse(false);
    }

    public boolean hasGuiPacket() {

        return true;
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
