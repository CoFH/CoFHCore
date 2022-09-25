package cofh.core.block;

import cofh.core.block.entity.TileCoFH;
import cofh.core.util.helpers.ChatHelper;
import cofh.lib.api.block.IDismantleable;
import cofh.lib.api.block.entity.ITickableTile;
import cofh.lib.api.block.entity.ITileCallback;
import cofh.lib.api.item.IPlacementItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.raytracer.RayTracer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class TileBlockCoFH extends Block implements EntityBlock, IDismantleable {

    protected final Supplier<BlockEntityType<? extends TileCoFH>> blockEntityType;
    protected final Class<? extends TileCoFH> tileClass;

    public TileBlockCoFH(Properties builder, Class<? extends TileCoFH> tileClass, Supplier<BlockEntityType<? extends TileCoFH>> blockEntityType) {

        super(builder);
        this.blockEntityType = blockEntityType;
        this.tileClass = tileClass;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return blockEntityType.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> actualType) {

        return ITickableTile.createTicker(level, actualType, blockEntityType.get(), tileClass);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (Utils.isClientWorld(worldIn)) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (!(tile instanceof TileCoFH) || tile.isRemoved()) {
            return InteractionResult.PASS;
        }
        if (!((TileCoFH) tile).canPlayerChange(player) && SecurityHelper.hasSecurity(tile)) {
            ChatHelper.sendIndexedChatMessageToPlayer(player, new TranslatableComponent("info.cofh.secure_warning", SecurityHelper.getOwnerName(tile)));
            return InteractionResult.PASS;
        }
        if (Utils.isWrench(player.getItemInHand(handIn))) {
            if (player.isSecondaryUseActive()) {
                if (canDismantle(worldIn, pos, state, player)) {
                    dismantleBlock(worldIn, pos, state, hit, player, false);
                    return InteractionResult.SUCCESS;
                }
            } else {
                BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
                if (rotState != state) {
                    worldIn.setBlockAndUpdate(pos, rotState);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (onBlockActivatedDelegate(worldIn, pos, state, player, handIn, hit)) {
            return InteractionResult.SUCCESS;
        }
        if (((TileCoFH) tile).canOpenGui()) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) tile, tile.getBlockPos());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected boolean onBlockActivatedDelegate(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult result) {

        TileCoFH tile = (TileCoFH) world.getBlockEntity(pos);
        if (tile == null || !tile.canPlayerChange(player)) {
            return false;
        }
        return tile.onActivatedDelegate(world, pos, state, player, hand, result);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        TileCoFH tile = (TileCoFH) worldIn.getBlockEntity(pos);
        if (tile != null) {
            tile.neighborChanged(blockIn, fromPos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (!(placer instanceof Player player) || Utils.isClientWorld(worldIn)) {
            return;
        }
        ItemStack offhand = placer.getOffhandItem();
        if (!offhand.isEmpty() && offhand.getItem() instanceof IPlacementItem) {
            ((IPlacementItem) offhand.getItem()).onBlockPlacement(offhand, new UseOnContext(player, InteractionHand.OFF_HAND, RayTracer.retrace(player)));
        }
        TileCoFH tile = (TileCoFH) worldIn.getBlockEntity(pos);
        if (tile != null) {
            tile.onPlacedBy(worldIn, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof TileCoFH) {
                ((TileCoFH) tile).onReplaced(state, worldIn, pos, newState);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {

        BlockEntity tile = worldIn.getBlockEntity(pos);
        return tile instanceof ITileCallback ? ((ITileCallback) tile).getComparatorInputOverride() : super.getAnalogOutputSignal(blockState, worldIn, pos);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {

        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof TileCoFH && !tile.isRemoved()) {
            if (!((TileCoFH) tile).canPlayerChange(player)) {
                return -1;
            }
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {

        ItemStack stack = super.getCloneItemStack(worldIn, pos, state);
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof ITileCallback) {
            ((ITileCallback) tile).createItemStackTag(stack);
        }
        return stack;
    }

    // region IDismantleable
    @Override
    public boolean canDismantle(Level world, BlockPos pos, BlockState state, Player player) {

        if (world.getBlockEntity(pos) instanceof TileCoFH tile) {
            return tile.canPlayerChange(player);
        }
        return false;
    }
    // endregion
}
