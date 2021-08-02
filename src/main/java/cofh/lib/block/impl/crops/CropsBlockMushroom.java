package cofh.lib.block.impl.crops;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {

        return state.isIn(Blocks.MYCELIUM) || state.isIn(Blocks.PODZOL);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        int age = getAge(state);
        if (age < getMaxAge() && ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt(20 - age) == 0)) {
            int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
            worldIn.setBlockState(pos, withAge(newAge), newAge == getMaxAge() ? 3 : 2);
            ForgeHooks.onCropsGrowPost(worldIn, pos, state);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

        return MUSHROOMS_BY_AGE[MathHelper.clamp(state.get(getAgeProperty()), 0, MUSHROOMS_BY_AGE.length - 1)];
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
    protected int getBonemealAgeIncrease(World worldIn) {

        return MathHelper.nextInt(worldIn.rand, 0, 2);
    }

    // region IGrowable
    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {

        return false;
    }
    // endregion
}
