package cofh.lib.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITileLocation {

    Block block();

    BlockState state();

    BlockPos pos();

    World world();

}
