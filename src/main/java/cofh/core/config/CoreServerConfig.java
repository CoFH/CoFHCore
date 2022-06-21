package cofh.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public class CoreServerConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Gameplay");

        builder.push("Fishing");

        enableFishingExhaustion = builder
                .comment("If TRUE, Fishing will cause exhaustion.")
                .define("Fishing Exhaustion", enableFishingExhaustion);
        amountFishingExhaustion = builder
                .comment("This option sets the amount of exhaustion caused by fishing, if enabled.")
                .defineInRange("Fishing Exhaustion Amount", amountFishingExhaustion, 0.0D, 10.0D);

        builder.pop();

        builder.push("World");

        enableSaplingGrowthMod = builder
                .comment("If TRUE, Sapling growth will be slowed by a configurable factor.")
                .define("Sapling Growth Reduction", enableSaplingGrowthMod);
        amountSaplingGrowthMod = builder
                .comment("This option sets the growth factor for saplings - they will only grow 1 in N times.")
                .defineInRange("Sapling Growth Reduction Factor", amountSaplingGrowthMod, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.pop();
    }

    public static Supplier<Boolean> enableFishingExhaustion = () -> false;
    public static Supplier<Double> amountFishingExhaustion = () -> 0.125;

    public static Supplier<Boolean> enableSaplingGrowthMod = () -> false;
    public static Supplier<Integer> amountSaplingGrowthMod = () -> 4;

}
