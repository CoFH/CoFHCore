/*
package cofh.core.world.gen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.BooleanSupplier;

// TODO Lemming: ConfiguredFeature is now a Record class which means we cannot extend it as its implicitly final.
public class ConfiguredFeatureCoFH<FC extends FeatureConfiguration, F extends Feature<FC>> extends ConfiguredFeature<FC, F> {

    private final BooleanSupplier enabled;

    private float chance = 1.0F;

    public ConfiguredFeatureCoFH(F feature, FC config, BooleanSupplier enabled) {

        super(feature, config);
        this.enabled = enabled;
    }

    public ConfiguredFeatureCoFH<FC, F> setChance(float chance) {

        this.chance = chance;
        return this;
    }

    @Override
    public boolean place(@Nonnull WorldGenLevel reader, @Nonnull ChunkGenerator chunkGenerator, @Nonnull Random rand, @Nonnull BlockPos pos) {

        if (enabled.getAsBoolean() && rand.nextFloat() < chance) {
            return super.place(reader, chunkGenerator, rand, pos);
        }
        return false;
    }

}
*/
