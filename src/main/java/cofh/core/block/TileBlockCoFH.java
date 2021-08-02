package cofh.core.block;

import cofh.core.tileentity.TileCoFH;
import cofh.core.util.helpers.ChatHelper;
import cofh.lib.block.IDismantleable;
import cofh.lib.item.IPlacementItem;
import cofh.lib.tileentity.ITileCallback;
import cofh.lib.util.RayTracer;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class TileBlockCoFH extends Block implements IDismantleable {

    protected final Supplier<? extends TileCoFH> supplier;

    public TileBlockCoFH(Properties builder, Supplier<? extends TileCoFH> supplier) {

        super(builder);
        this.supplier = supplier;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {

        return supplier.get().worldContext(state, world);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (Utils.isClientWorld(worldIn)) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (!(tile instanceof TileCoFH) || tile.isRemoved()) {
            return ActionResultType.PASS;
        }
        if (!((TileCoFH) tile).canPlayerChange(player) && SecurityHelper.hasSecurity(tile)) {
            ChatHelper.sendIndexedChatMessageToPlayer(player, new TranslationTextComponent("info.cofh.secure_warning", SecurityHelper.getOwnerName(tile)));
            return ActionResultType.PASS;
        }
        if (Utils.isWrench(player.getHeldItem(handIn).getItem())) {
            if (player.isSecondaryUseActive()) {
                if (canDismantle(worldIn, pos, state, player)) {
                    dismantleBlock(worldIn, pos, state, hit, player, false);
                    return ActionResultType.SUCCESS;
                }
            } else {
                BlockState rotState = rotate(state, worldIn, pos, Rotation.CLOCKWISE_90);
                if (rotState != state) {
                    worldIn.setBlockState(pos, rotState);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        if (onBlockActivatedDelegate(worldIn, pos, state, player, handIn, hit)) {
            return ActionResultType.SUCCESS;
        }
        if (((TileCoFH) tile).canOpenGui()) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tile, tile.getPos());
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    protected boolean onBlockActivatedDelegate(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        TileCoFH tile = (TileCoFH) world.getTileEntity(pos);
        if (tile == null || !tile.canPlayerChange(player)) {
            return false;
        }
        return tile.onActivatedDelegate(world, pos, state, player, hand, result);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        TileCoFH tile = (TileCoFH) worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.neighborChanged(blockIn, fromPos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

        if (!(placer instanceof PlayerEntity) || Utils.isClientWorld(worldIn)) {
            return;
        }
        ItemStack offhand = placer.getHeldItemOffhand();
        if (!offhand.isEmpty() && offhand.getItem() instanceof IPlacementItem) {
            PlayerEntity player = (PlayerEntity) placer;
            ((IPlacementItem) offhand.getItem()).onBlockPlacement(offhand, new ItemUseContext(player, Hand.OFF_HAND, RayTracer.retrace(player)));
        }
        TileCoFH tile = (TileCoFH) worldIn.getTileEntity(pos);
        if (tile != null) {
            tile.onPlacedBy(worldIn, pos, state, placer, stack);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileCoFH) {
                ((TileCoFH) tile).onReplaced(state, worldIn, pos, newState);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {

        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof ITileCallback ? ((ITileCallback) tile).getComparatorInputOverride() : super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {

        ItemStack stack = super.getItem(worldIn, pos, state);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof ITileCallback) {
            ((ITileCallback) tile).createItemStackTag(stack);
        }
        return stack;
    }

    // region IDismantleable
    @Override
    public boolean canDismantle(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileCoFH) {
            return ((TileCoFH) tile).canPlayerChange(player);
        }
        return false;
    }
    // endregion
}
