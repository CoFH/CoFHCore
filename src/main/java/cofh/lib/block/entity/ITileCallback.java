package cofh.lib.block.entity;

import cofh.lib.util.IInventoryCallback;
import cofh.lib.util.control.ISecurable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Mostly a sneaky way to reduce some stupid but useful boilerplate. :)
 */
public interface ITileCallback extends IInventoryCallback, ITileLocation {

    default int invSize() {

        return 0;
    }

    default int augSize() {

        return 0;
    }

    default int getComparatorInputOverride() {

        return 0;
    }

    default int getLightValue() {

        return 0;
    }

    default void neighborChanged(Block blockIn, BlockPos fromPos) {

    }

    default ItemStack createItemStackTag(ItemStack stack) {

        return stack;
    }

    default void callBlockUpdate() {

        if (world() == null) {
            return;
        }
        BlockState state = world().getBlockState(pos());
        world().sendBlockUpdated(pos(), state, state, 3);
    }

    default void callNeighborStateChange() {

        if (world() == null) {
            return;
        }
        world().updateNeighborsAt(pos(), block());
    }

    default void onControlUpdate() {

    }

    default void onPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

    }

    default void onReplaced(BlockState state, Level worldIn, BlockPos pos, BlockState newState) {

    }

    default boolean canOpenGui() {

        return this instanceof MenuProvider;
    }

    default boolean canPlayerChange(Player player) {

        return !(this instanceof ISecurable) || ((ISecurable) this).canAccess(player);
    }

    default boolean onWrench(Player player, Direction side) {

        return false;
    }

}
