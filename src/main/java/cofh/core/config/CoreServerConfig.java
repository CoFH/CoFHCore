package cofh.core.config;

import cofh.lib.config.IBaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoreServerConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Gameplay");

        builder.push("Fishing");

        cfgEnableFishingExhaustion = builder
                .comment("If TRUE, Fishing will cause exhaustion.")
                .define("Fishing Exhaustion", enableFishingExhaustion);
        cfgAmountFishingExhaustion = builder
                .comment("This option sets the amount of exhaustion caused by fishing, if enabled.")
                .defineInRange("Fishing Exhaustion Amount", amountFishingExhaustion, 0.0D, 10.0D);

        builder.pop();

        builder.push("World");

        cfgEnableSaplingGrowthMod = builder
                .comment("If TRUE, Sapling growth will be slowed by a configurable factor.")
                .define("Sapling Growth Reduction", enableSaplingGrowthMod);
        cfgAmountSaplingGrowthMod = builder
                .comment("This option sets the growth factor for saplings - they will only grow 1 in N times.")
                .defineInRange("Sapling Growth Reduction Factor", amountSaplingGrowthMod, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.pop();
    }

    @Override
    public void refresh() {

        enableFishingExhaustion = cfgEnableFishingExhaustion.get();
        amountFishingExhaustion = cfgAmountFishingExhaustion.get().floatValue();

        enableSaplingGrowthMod = cfgEnableSaplingGrowthMod.get();
        amountSaplingGrowthMod = cfgAmountSaplingGrowthMod.get();
    }

    // region VARIABLES
    public static boolean enableFishingExhaustion = false;
    public static float amountFishingExhaustion = 0.125F;

    public static boolean enableSaplingGrowthMod = false;
    public static int amountSaplingGrowthMod = 4;

    private ForgeConfigSpec.BooleanValue cfgEnableFishingExhaustion;
    private ForgeConfigSpec.DoubleValue cfgAmountFishingExhaustion;

    private ForgeConfigSpec.BooleanValue cfgEnableSaplingGrowthMod;
    private ForgeConfigSpec.IntValue cfgAmountSaplingGrowthMod;
    // endregion
}
