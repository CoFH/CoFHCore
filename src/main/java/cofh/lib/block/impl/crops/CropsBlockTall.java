package cofh.lib.block.impl.crops;

import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.Random;

import static cofh.lib.util.constants.Constants.*;

public class CropsBlockTall extends CropsBlockCoFH {

    public CropsBlockTall(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
        this.setDefaultState(this.stateContainer.getBaseState().with(this.getAgeProperty(), 0).with(TOP, false));
    }

    public CropsBlockTall(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropsBlockTall(Properties builder) {

        this(builder, 9, 1.25F);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        super.fillStateContainer(builder);
        builder.add(TOP);
    }

    protected boolean isTop(BlockState state) {

        return state.get(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

        int age = state.get(getAgeProperty()) - (isTop(state) ? 2 : 0);
        return TALL_CROPS_BY_AGE[MathHelper.clamp(age, 0, TALL_CROPS_BY_AGE.length - 1)];
    }

    @Override
    public IntegerProperty getAgeProperty() {

        return AGE_0_9;
    }

    @Override
    public int getMaxAge() {

        return 9;
    }

    protected int getTallAge() {

        return 4;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        if (!worldIn.isAreaLoaded(pos, 1) || isTop(state) || !isValidPosition(state, worldIn, pos)) {
            return;
        }
        if (worldIn.getLightSubtracted(pos, 0) >= growLight) {
            if (!canHarvest(state)) {
                int age = getAge(state);
                float growthChance = MathHelper.maxF(getGrowthChance(this, worldIn, pos) * growMod, 0.1F);
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
                    int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
                    worldIn.setBlockState(pos, withAge(newAge), 2);
                    if (newAge >= getTallAge()) {
                        worldIn.setBlockState(pos.up(), withAge(newAge).with(TOP, true), 2);
                    }
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {

        if (isTop(state)) {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        }
        if (getAge(state) >= getTallAge()) {
            return worldIn.getBlockState(pos.up()).getBlock() == this;
        }
        return pos.getY() < 255 && super.isValidPosition(state, worldIn, pos) && (worldIn.isAirBlock(pos.up()));
    }

    // region IGrowable
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {

        return !isTop(state);
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {

        if (canHarvest(state) || isTop(state) || !isValidPosition(state, worldIn, pos)) {
            return;
        }
        BlockPos above = pos.up();
        if (!worldIn.isAirBlock(above) && worldIn.getBlockState(above).getBlock() != this) {
            return;
        }
        int newAge = getAge(state) + getBonemealAgeIncrease(worldIn);
        newAge = Math.min(newAge, getMaxAge());
        worldIn.setBlockState(pos, withAge(newAge), 2);
        if (newAge >= getTallAge()) {
            worldIn.setBlockState(above, withAge(newAge).with(TOP, true), 2);
        }
    }
    // endregion

    // region IHarvestable
    @Override
    public boolean harvest(World world, BlockPos pos, BlockState state, int fortune) {

        if (!canHarvest(state)) {
            return false;
        }
        if (Utils.isClientWorld(world)) {
            return true;
        }
        if (getPostHarvestAge() >= 0) {
            Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos);
            if (isTop(state)) {
                world.setBlockState(pos, this.withAge(getPostHarvestAge() + getTallAge()), 2);
                world.setBlockState(pos.down(), this.withAge(getPostHarvestAge()), 2);
                Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos.down());
            } else {
                world.setBlockState(pos, this.withAge(getPostHarvestAge()), 2);
                world.setBlockState(pos.up(), this.withAge(getPostHarvestAge() + getTallAge()), 2);
                Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos.up());
            }
        } else {
            world.destroyBlock(pos, true);
            world.destroyBlock(isTop(state) ? pos.down() : pos.up(), true);
        }
        return true;
    }
    // endregion
}
