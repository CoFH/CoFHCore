package cofh.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public class CoreEnchantConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        String treasure = "This sets whether or not the Enchantment is considered a 'treasure' enchantment.";
        String level = "This option adjusts the maximum allowable level for the Enchantment.";

        builder.push("Enchantments");

        improvedFeatherFalling = builder
                .comment("If TRUE, Feather Falling will prevent Farmland from being trampled. This option will work with alternative versions (overrides) of Feather Falling.")
                .define("Improved Feather Falling", improvedFeatherFalling);

        improvedMending = builder
                .comment("If TRUE, Mending behavior is altered so that Experience Orbs always repair items if possible, and the most damaged item is prioritized. This option may not work with alternative versions (overrides) of Mending.")
                .define("Improved Mending", improvedMending);

        //        builder.push("Holding");
        //        enableHolding = builder
        //                .comment("If TRUE, the Holding Enchantment is available for various Storage Items and Blocks.")
        //                .define("Enable", true);
        //        treasureHolding = builder
        //                .comment(treasure)
        //                .define("Treasure", false);
        //        levelHolding = builder
        //                .comment(level)
        //                .defineInRange("Max Level", 4, 1, MAX_ENCHANT_LEVEL);
        //        builder.pop();

        builder.pop();
    }

    //
    //    @Override
    //    public void refresh() {
    //
    //        EnchantmentCoFH encHolding = HOLDING.get();
    //        encHolding.setEnable(enableHolding.get());
    //        encHolding.setTreasureEnchantment(treasureHolding.get());
    //        encHolding.setMaxLevel(levelHolding.get());
    //    }

    public static Supplier<Boolean> improvedFeatherFalling = () -> true;
    public static Supplier<Boolean> improvedMending = () -> true;

}
