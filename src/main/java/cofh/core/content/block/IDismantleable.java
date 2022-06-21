package cofh.core.content.block;

import cofh.lib.api.block.entity.ITileCallback;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * Implemented on Blocks which have some method of being instantly dismantled.
 *
 * @author King Lemming
 */
public interface IDismantleable extends IForgeBlock {

    /**
     * Dismantles the block. If returnDrops is true, the drop(s) should be placed into the player's inventory.
     */
    default void dismantleBlock(Level world, BlockPos pos, BlockState state, HitResult target, Player player, boolean returnDrops) {

        ItemStack dropBlock = this.getCloneItemStack(state, target, world, pos, player);
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        if (!returnDrops || player == null || !player.addItem(dropBlock)) {
            Utils.dropDismantleStackIntoWorld(dropBlock, world, pos);
        }
    }

    /**
     * Return true if the block can be dismantled. The criteria for this is entirely up to the block.
     */
    default boolean canDismantle(Level world, BlockPos pos, BlockState state, Player player) {

        if (world.getBlockEntity(pos) instanceof ITileCallback tile) {
            return tile.canPlayerChange(player);
        }
        return true;
    }

}
