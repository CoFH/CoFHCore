package cofh.lib.capability;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Implement this interface as a capability for an Item which should be compatible with CoFH's AOE overlay rendering/handling.
 * You can get really clever and add your own custom behavior by overriding the "getAreaEffectBlocks" method. By default, this method is a passthrough for CoFH's vanilla handling.
 *
 * @author King Lemming
 */
public interface IAreaEffect {

    ImmutableList<BlockPos> getAreaEffectBlocks(BlockPos pos, PlayerEntity player);

}
