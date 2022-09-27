package cofh.core.config.world;

import cofh.core.config.IBaseConfig;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FeatureConfig implements IBaseConfig {

    private Set<BiomeDictionary.Type> storedBiomes = null;
    private Set<BiomeDictionary.Type> storedRestrictions = null;

    protected List<BiomeDictionary.Type> biomes;
    protected List<BiomeDictionary.Type> biomeRestrictions;

    protected ForgeConfigSpec.ConfigValue<String> configBiomes;
    protected ForgeConfigSpec.ConfigValue<String> configBiomeRestrictions;

    protected Supplier<ConfiguredStructureFeature<?, ?>> structure = null;

    public FeatureConfig(List<BiomeDictionary.Type> biomeTypes, List<BiomeDictionary.Type> biomeRestrictions) {

        super();
        this.biomes = biomeTypes;
        this.biomeRestrictions = biomeRestrictions;
    }

    public boolean isFeature() {

        return true;
    }

    public abstract GenerationStep.Decoration getStage();

    public abstract boolean shouldSpawn();

    public Set<BiomeDictionary.Type> getBiomes() {

        if (storedBiomes == null) {
            storedBiomes = Stream.of(configBiomes.get().split(",")).map(BiomeDictionary.Type::getType).collect(Collectors.toSet());
        }
        return storedBiomes;
    }

    public Set<BiomeDictionary.Type> getBiomeRestrictions() {

        if (storedRestrictions == null) {
            storedRestrictions = Stream.of(configBiomeRestrictions.get().split(",")).map(BiomeDictionary.Type::getType).collect(Collectors.toSet());
        }
        return storedRestrictions;
    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

    }

    @Override
    public void refresh() {

    }

}
