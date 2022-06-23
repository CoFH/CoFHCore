package cofh.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import static cofh.lib.util.Constants.FUNGUS;
import static cofh.lib.util.constants.BlockStatePropertiesCoFH.AGE_0_4;

public class CropBlockMushroom extends CropBlockCoFH {

    public static final VoxelShape[] MUSHROOMS_BY_AGE = new VoxelShape[]{
            box(4.0D, 0.0D, 4.0D, 12.0D, 6.0D, 12.0D),
            box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D),
            box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
            box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
            box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D)};

    public CropBlockMushroom(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
    }

    public CropBlockMushroom(Properties builder, int growLight, float growMod) {

        this(builder, FUNGUS, growLight, growMod);
    }

    public CropBlockMushroom(Properties properties) {

        this(properties, 0, 1.0F);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {

        return state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {

        int age = getAge(state);
        if (age < getMaxAge() && ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt(20 + age) == 0)) {
            int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
            worldIn.setBlock(pos, getStateForAge(newAge), newAge == getMaxAge() ? 3 : 2);
            ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return MUSHROOMS_BY_AGE[Mth.clamp(state.getValue(getAgeProperty()), 0, MUSHROOMS_BY_AGE.length - 1)];
    }

    @Override
    public IntegerProperty getAgeProperty() {

        return AGE_0_4;
    }

    @Override
    public int getMaxAge() {

        return 4;
    }

    @Override
    protected int getBonemealAgeIncrease(Level worldIn) {

        return Mth.nextInt(worldIn.random, 0, 2);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {

        BlockPos blockpos = pos.below();
        if (state.getBlock() == this) { //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
            return worldIn.getBlockState(blockpos).canSustainPlant(worldIn, blockpos, Direction.UP, this);
        }
        return this.mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    // region BonemealableBlock
    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {

        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {

        return false;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {

    }
    // endregion
}
