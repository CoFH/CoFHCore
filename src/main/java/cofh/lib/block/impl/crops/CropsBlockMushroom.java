package cofh.lib.block.impl.crops;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.Random;

import static cofh.lib.util.constants.Constants.*;

public class CropsBlockMushroom extends CropsBlockCoFH {

    public CropsBlockMushroom(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
    }

    public CropsBlockMushroom(Properties builder, int growLight, float growMod) {

        this(builder, FUNGUS, growLight, growMod);
    }

    public CropsBlockMushroom(Properties properties) {

        this(properties, 0, 1.0F);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {

        return state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {

        int age = getAge(state);
        if (age < getMaxAge() && ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt(20 - age) == 0)) {
            int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
            worldIn.setBlock(pos, getStateForAge(newAge), newAge == getMaxAge() ? 3 : 2);
            ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return MUSHROOMS_BY_AGE[MathHelper.clamp(state.getValue(getAgeProperty()), 0, MUSHROOMS_BY_AGE.length - 1)];
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

        return MathHelper.nextInt(worldIn.random, 0, 2);
    }

    // region IGrowable
    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {

        return false;
    }
    // endregion
}
