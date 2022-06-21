package cofh.core.content.block;

import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;

public class GlossedMagmaBlock extends MagmaBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);

    public GlossedMagmaBlock(Properties builder) {

        super(builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(AGE);
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {

        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            Material material = worldIn.getBlockState(pos.below()).getMaterial();
            if (material.blocksMotion() || material.isLiquid()) {
                worldIn.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (blockIn == this && this.shouldMelt(worldIn, pos, 2)) {
            this.turnIntoLava(state, worldIn, pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {

        this.tick(state, worldIn, pos, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {

        if ((rand.nextInt(9) == 0 || this.shouldMelt(worldIn, pos, 4)) && this.slightlyMelt(state, worldIn, pos)) {
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
            for (Direction direction : BlockHelper.DIR_VALUES) {
                blockpos$mutable.setWithOffset(pos, direction);
                BlockState blockstate = worldIn.getBlockState(blockpos$mutable);
                if (blockstate.is(this) && !this.slightlyMelt(blockstate, worldIn, blockpos$mutable)) {
                    worldIn.scheduleTick(blockpos$mutable, this, Mth.nextInt(rand, 20, 40));
                }
            }
        } else {
            worldIn.scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {

        return ItemStack.EMPTY;
    }

    // region HELPERS
    protected void turnIntoLava(BlockState state, Level worldIn, BlockPos pos) {

        worldIn.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        worldIn.neighborChanged(pos, Blocks.LAVA, pos);
    }

    protected boolean shouldMelt(BlockGetter worldIn, BlockPos pos, int neighborsRequired) {

        int i = 0;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            blockpos$mutable.setWithOffset(pos, direction);
            if (worldIn.getBlockState(blockpos$mutable).is(this)) {
                ++i;
                if (i >= neighborsRequired) {
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean slightlyMelt(BlockState state, Level worldIn, BlockPos pos) {

        int i = state.getValue(AGE);
        if (i < 3) {
            worldIn.setBlock(pos, state.setValue(AGE, i + 1), 2);
            return false;
        } else {
            this.turnIntoLava(state, worldIn, pos);
            return true;
        }
    }
    // endregion
}
