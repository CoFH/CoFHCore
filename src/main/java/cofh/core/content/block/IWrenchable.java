package cofh.core.content.block;

import cofh.lib.content.block.entity.ITileCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IWrenchable extends IForgeBlock {

    /**
     * Wrenches the block.
     */
    default void wrenchBlock(Level world, BlockPos pos, BlockState state, HitResult target, Player player) {

        BlockState rotState = this.rotate(state, world, pos, Rotation.CLOCKWISE_90);
        if (rotState != state) {
            world.setBlockAndUpdate(pos, rotState);
        }
    }

    /**
     * Return true if the block can be wrenched. The criteria for this is entirely up to the block.
     */
    default boolean canWrench(Level world, BlockPos pos, BlockState state, Player player) {

        if (world.getBlockEntity(pos) instanceof ITileCallback tile) {
            return tile.canPlayerChange(player);
        }
        return true;
    }

}
