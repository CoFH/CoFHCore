package cofh.lib.block;

import cofh.lib.tileentity.ITileCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface IWrenchable extends IForgeBlock {

    /**
     * Wrenches the block.
     */
    default void wrenchBlock(World world, BlockPos pos, BlockState state, RayTraceResult target, PlayerEntity player) {

        BlockState rotState = this.rotate(state, world, pos, Rotation.CLOCKWISE_90);
        if (rotState != state) {
            world.setBlockState(pos, rotState);
        }
    }

    /**
     * Return true if the block can be wrenched. The criteria for this is entirely up to the block.
     */
    default boolean canWrench(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITileCallback) {
            return ((ITileCallback) tile).canPlayerChange(player);
        }
        return true;
    }

}
