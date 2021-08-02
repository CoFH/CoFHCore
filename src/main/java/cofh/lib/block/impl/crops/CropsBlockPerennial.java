package cofh.lib.block.impl.crops;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.state.IntegerProperty;
import net.minecraft.world.World;
import net.minecraftforge.common.PlantType;

import static cofh.lib.util.constants.Constants.AGE_0_10;

public class CropsBlockPerennial extends CropsBlockCoFH {

    public static final int DEFAULT_POST_HARVEST_AGE = 7;

    public int postHarvestAge = DEFAULT_POST_HARVEST_AGE;

    public CropsBlockPerennial(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
    }

    public CropsBlockPerennial(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropsBlockPerennial(Properties builder) {

        this(builder, 9, 0.80F);
    }

    public CropsBlockPerennial postHarvestAge(int postHarvestAge) {

        this.postHarvestAge = postHarvestAge;
        return this;
    }

    @Override
    public IntegerProperty getAgeProperty() {

        return AGE_0_10;
    }

    @Override
    public int getMaxAge() {

        return 10;
    }

    @Override
    protected int getPostHarvestAge() {

        return postHarvestAge;
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {

        return MathHelper.nextInt(worldIn.rand, 1, 3);
    }

}
