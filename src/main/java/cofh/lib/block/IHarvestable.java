package cofh.lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static net.minecraft.enchantment.Enchantments.FORTUNE;

public interface IHarvestable {

    boolean canHarvest(BlockState state);

    boolean harvest(World world, BlockPos pos, BlockState state, int fortune);

    default boolean harvest(World world, BlockPos pos, BlockState state, @Nonnull PlayerEntity player) {

        return harvest(world, pos, state, getItemEnchantmentLevel(FORTUNE, player.getHeldItemMainhand()));
    }

}
