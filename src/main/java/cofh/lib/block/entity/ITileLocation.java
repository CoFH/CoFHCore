package cofh.lib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ITileLocation {

    Block block();

    BlockState state();

    BlockPos pos();

    Level world();

}
