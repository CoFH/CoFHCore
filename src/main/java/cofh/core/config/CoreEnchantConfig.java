package cofh.core.config;

import cofh.lib.config.IBaseConfig;
import cofh.lib.enchantment.EnchantmentCoFH;
import net.minecraftforge.common.ForgeConfigSpec;

import static cofh.lib.util.constants.Constants.MAX_ENCHANT_LEVEL;
import static cofh.lib.util.references.CoreReferences.HOLDING;

public class CoreEnchantConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        String treasure = "This sets whether or not the Enchantment is considered a 'treasure' enchantment.";
        String level = "This option adjusts the maximum allowable level for the Enchantment.";

        builder.push("Enchantments");

        serverImprovedFeatherFalling = builder
                .comment("If TRUE, Feather Falling will prevent Farmland from being trampled. This option will work with alternative versions (overrides) of Feather Falling.")
                .define("Improved Feather Falling", improvedFeatherFalling);

        serverImprovedMending = builder
                .comment("If TRUE, Mending behavior is altered so that Experience Orbs always repair items if possible, and the most damaged item is prioritized. This option may not work with alternative versions (overrides) of Mending.")
                .define("Improved Mending", improvedMending);

        builder.push("Holding");
        enableHolding = builder
                .comment("If TRUE, the Holding Enchantment is available for various Storage Items and Blocks.")
                .define("Enable", true);
        treasureHolding = builder
                .comment(treasure)
                .define("Treasure", false);
        levelHolding = builder
                .comment(level)
                .defineInRange("Max Level", 4, 1, MAX_ENCHANT_LEVEL);
        builder.pop();

        builder.pop();
    }

    @Override
    public void refresh() {

        improvedFeatherFalling = serverImprovedFeatherFalling.get();
        improvedMending = serverImprovedMending.get();

        if (HOLDING instanceof EnchantmentCoFH) {
            ((EnchantmentCoFH) HOLDING).setEnable(enableHolding.get());
            ((EnchantmentCoFH) HOLDING).setTreasureEnchantment(treasureHolding.get());
            ((EnchantmentCoFH) HOLDING).setMaxLevel(levelHolding.get());
        }
    }

    // region VARIABLES
    public static boolean improvedFeatherFalling = true;
    public static boolean improvedMending = true;

    private ForgeConfigSpec.BooleanValue serverImprovedFeatherFalling;
    private ForgeConfigSpec.BooleanValue serverImprovedMending;

    private ForgeConfigSpec.BooleanValue enableHolding;
    private ForgeConfigSpec.BooleanValue treasureHolding;
    private ForgeConfigSpec.IntValue levelHolding;
    // endregion
}
