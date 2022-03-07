package cofh.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public interface IHarvestable {

    boolean canHarvest(BlockState state);

    boolean harvest(Level world, BlockPos pos, BlockState state, @Nonnull Player player, boolean replant);

}
