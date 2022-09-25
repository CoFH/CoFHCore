package cofh.lib.config.world;

import cofh.lib.config.IBaseConfig;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cofh.lib.util.Constants.FALSE;

public class MobConfig implements IBaseConfig {

    public static final MobConfig EMPTY_CONFIG = new MobConfig("invalid", 0, 0, 0, List.of(), FALSE);

    protected String name;
    protected int chance;
    protected int min;
    protected int max;
    protected List<String> biomes;
    protected BiomeDictionary.Type restriction;
    protected BooleanSupplier enable;

    protected ForgeConfigSpec.IntValue configChance;
    protected ForgeConfigSpec.IntValue configMin;
    protected ForgeConfigSpec.IntValue configMax;
    protected ForgeConfigSpec.ConfigValue<String> configBiomes;

    public MobConfig(String name, int chance, int min, int max, List<String> biomes, BooleanSupplier enable) {

        this(name, chance, min, max, biomes, BiomeDictionary.Type.OVERWORLD);
    }

    public MobCategory getClassification() {

        return MobCategory.CREATURE;
    }

    public MobConfig(String name, int chance, int min, int max, List<String> biomes, BiomeDictionary.Type restriction) {

        super();
        this.name = name;
        this.chance = chance;
        this.min = min;
        this.max = max;
        this.biomes = biomes;
        this.restriction = restriction;
    }

    public BiomeDictionary.Type getRestriction() {

        return restriction;
    }

    public int getChance() {

        return configChance.get();
    }

    public int getMin() {

        return configMin.get();
    }

    public int getMax() {

        return configMax.get();
    }

    private List<BiomeDictionary.Type> cachedBiomes = null;

    public List<BiomeDictionary.Type> getBiomes() {

        if (cachedBiomes == null) {
            cachedBiomes = Stream.of(configBiomes.get().split(",")).map(BiomeDictionary.Type::getType).collect(Collectors.toList());
        }
        return cachedBiomes;
    }

    public boolean shouldRegister() {

        return getChance() > 0;
    }

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.comment(name + " spawn config.").push(name + "_spawn");
        configChance = builder.comment("Chance to spawn (set to 0 to disable).").defineInRange("spawnChance", chance, 0, 256);
        configMin = builder.comment("Min to spawn in a group.").defineInRange("min", min, 0, 256);
        configMax = builder.comment("Max to spawn in a group.").defineInRange("max", max, 0, 256);

        StringBuilder sb = new StringBuilder();
        biomes.forEach(biome -> {
            sb.append(biome);
            sb.append(",");
        });
        configBiomes = builder.comment("List of biome types to spawn.").define("biomes", sb.toString());

        builder.pop();
    }

    @Override
    public void refresh() {

        cachedBiomes = null;
    }

}