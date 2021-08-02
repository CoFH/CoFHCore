package cofh.core.block;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;

public class GlossedMagmaBlock extends MagmaBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public GlossedMagmaBlock(Properties builder) {

        super(builder);
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(AGE);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {

        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            Material material = worldIn.getBlockState(pos.down()).getMaterial();
            if (material.blocksMovement() || material.isLiquid()) {
                worldIn.setBlockState(pos, Blocks.LAVA.getDefaultState());
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (blockIn == this && this.shouldMelt(worldIn, pos, 2)) {
            this.turnIntoLava(state, worldIn, pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        this.tick(state, worldIn, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

        if ((rand.nextInt(9) == 0 || this.shouldMelt(worldIn, pos, 4)) && this.slightlyMelt(state, worldIn, pos)) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                blockpos$mutable.setAndMove(pos, direction);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (blockstate.isIn(this) && !this.slightlyMelt(blockstate, worldIn, blockpos$mutable)) {
                    worldIn.getPendingBlockTicks().scheduleTick(blockpos$mutable, this, net.minecraft.util.math.MathHelper.nextInt(rand, 20, 40));
                }
            }
        } else {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {

        return ItemStack.EMPTY;
    }

    // region HELPERS
    protected void turnIntoLava(BlockState state, World worldIn, BlockPos pos) {

        worldIn.setBlockState(pos, Blocks.LAVA.getDefaultState());
        worldIn.neighborChanged(pos, Blocks.LAVA, pos);
    }

    protected boolean shouldMelt(IBlockReader worldIn, BlockPos pos, int neighborsRequired) {

        int i = 0;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            blockpos$mutable.setAndMove(pos, direction);
            if (worldIn.getBlockState(blockpos$mutable).isIn(this)) {
                ++i;
                if (i >= neighborsRequired) {
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean slightlyMelt(BlockState state, World worldIn, BlockPos pos) {

        int i = state.get(AGE);
        if (i < 3) {
            worldIn.setBlockState(pos, state.with(AGE, i + 1), 2);
            return false;
        } else {
            this.turnIntoLava(state, worldIn, pos);
            return true;
        }
    }
    // endregion
}
