package cofh.lib.common.block;

import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.PlantType;

import static cofh.lib.util.constants.BlockStatePropertiesCoFH.AGE_0_10;

public class CropBlockPerennial extends CropBlockCoFH {

    public static final int DEFAULT_POST_HARVEST_AGE = 7;

    public int postHarvestAge = DEFAULT_POST_HARVEST_AGE;

    public CropBlockPerennial(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
    }

    public CropBlockPerennial(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropBlockPerennial(Properties builder) {

        this(builder, 9, 0.80F);
    }

    public CropBlockPerennial postHarvestAge(int postHarvestAge) {

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
    protected int getBonemealAgeIncrease(Level worldIn) {

        return Mth.nextInt(worldIn.random, 1, 3);
    }

}
