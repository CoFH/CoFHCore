package cofh.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;

public class CoreCommonConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Gameplay");

        returnDismantleDrops = builder
                .comment("If TRUE, dismantled blocks will be placed in the dismantling player's inventory if possible.")
                .define("Return Dismantle Drops", returnDismantleDrops);

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

    public static boolean returnDismantleDrops() {

        return returnDismantleDrops.get();
    }

    public static boolean enableFishingExhaustion() {

        return enableFishingExhaustion.get();
    }

    public static float amountFishingExhaustion() {

        return amountFishingExhaustion.get().floatValue();
    }

    public static boolean enableSaplingGrowthMod() {

        return enableSaplingGrowthMod.get();
    }

    public static int amountSaplingGrowthMod() {

        return amountSaplingGrowthMod.get();
    }

    private static Supplier<Boolean> returnDismantleDrops = FALSE;

    private static Supplier<Boolean> enableFishingExhaustion = FALSE;
    private static Supplier<Double> amountFishingExhaustion = () -> 0.125;

    private static Supplier<Boolean> enableSaplingGrowthMod = FALSE;
    private static Supplier<Integer> amountSaplingGrowthMod = () -> 4;

}
