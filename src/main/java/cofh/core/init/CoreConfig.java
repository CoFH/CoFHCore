package cofh.core.init;

import cofh.core.command.*;
import cofh.lib.enchantment.EnchantmentCoFH;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static cofh.lib.util.constants.Constants.MAX_ENCHANT_LEVEL;
import static cofh.lib.util.references.CoreReferences.HOLDING;

public class CoreConfig {

    private static boolean registered = false;

    public static void register() {

        if (registered) {
            return;
        }
        FMLJavaModLoadingContext.get().getModEventBus().register(CoreConfig.class);
        registered = true;

        genServerConfig();
        genClientConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    private CoreConfig() {

    }

    // region CONFIG SPEC
    private static final ForgeConfigSpec.Builder SERVER_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec serverSpec;

    private static final ForgeConfigSpec.Builder CLIENT_CONFIG = new ForgeConfigSpec.Builder();
    private static ForgeConfigSpec clientSpec;

    private static void genServerConfig() {

        SERVER_CONFIG.push("Commands");

        permissionCrafting = SERVER_CONFIG
                .comment("The required permission level for the '/cofh crafting' command.")
                .defineInRange("Crafting Permission Level", SubCommandCrafting.permissionLevel, 0, 4);
        permissionEnderChest = SERVER_CONFIG
                .comment("The required permission level for the '/cofh enderchest' command.")
                .defineInRange("EnderChest Permission Level", SubCommandEnderChest.permissionLevel, 0, 4);
        permissionHeal = SERVER_CONFIG
                .comment("The required permission level for the '/cofh heal' command.")
                .defineInRange("Heal Permission Level", SubCommandHeal.permissionLevel, 0, 4);
        permissionIgnite = SERVER_CONFIG
                .comment("The required permission level for the '/cofh ignite' command.")
                .defineInRange("Ignite Permission Level", SubCommandIgnite.permissionLevel, 0, 4);
        permissionRepair = SERVER_CONFIG
                .comment("The required permission level for the '/cofh repair' command.")
                .defineInRange("Repair Permission Level", SubCommandRepair.permissionLevel, 0, 4);

        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Enchantments");

        serverImprovedFeatherFalling = SERVER_CONFIG
                .comment("If TRUE, Feather Falling will prevent Farmland from being trampled. This option will work with alternative versions (overrides) of Feather Falling.")
                .define("Improved Feather Falling", improvedFeatherFalling);

        serverImprovedMending = SERVER_CONFIG
                .comment("If TRUE, Mending behavior is altered so that Experience Orbs always repair items if possible, and the most damaged item is prioritized. This option may not work with alternative versions (overrides) of Mending.")
                .define("Improved Mending", improvedMending);

        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("Fishing");

        serverEnableFishingExhaustion = SERVER_CONFIG
                .comment("If TRUE, Fishing will cause exhaustion.")
                .define("Fishing Exhaustion", enableFishingExhaustion);
        serverAmountFishingExhaustion = SERVER_CONFIG
                .comment("This option sets the amount of exhaustion caused by fishing, if enabled.")
                .defineInRange("Fishing Exhaustion Amount", amountFishingExhaustion, 0.0D, 10.0D);

        SERVER_CONFIG.pop();

        SERVER_CONFIG.push("World");

        serverEnableSaplingGrowthMod = SERVER_CONFIG
                .comment("If TRUE, Sapling growth will be slowed by a configurable factor.")
                .define("Sapling Growth Reduction", enableSaplingGrowthMod);
        serverAmountSaplingGrowthMod = SERVER_CONFIG
                .comment("This option sets the growth factor for saplings - they will only grow 1 in N times.")
                .defineInRange("Sapling Growth Reduction Factor", amountSaplingGrowthMod, 1, Integer.MAX_VALUE);

        SERVER_CONFIG.pop();

        serverSpec = SERVER_CONFIG.build();

        refreshServerConfig();
    }

    private static void genClientConfig() {

        CLIENT_CONFIG.push("Tooltips");

        clientEnableEnchantmentDescriptions = CLIENT_CONFIG
                .comment("If TRUE, Enchantment descriptions will be added to the tooltip for Enchanted Books containing only a single enchantment.")
                .define("Show Enchantment Descriptions", enableEnchantmentDescriptions);

        clientEnableItemDescriptions = CLIENT_CONFIG
                .comment("If TRUE, Item descriptions will be added to their tooltips if possible.")
                .define("Show Item Descriptions", enableItemDescriptions);

        clientEnableItemTags = CLIENT_CONFIG
                .comment("If TRUE and Advanced Tooltips are enabled (F3+H), Tags will be will be added to item tooltips if possible.")
                .define("Show Item Tags", enableItemTags);

        clientAlwaysShowDetails = CLIENT_CONFIG
                .comment("If TRUE, CoFH Items will always show full details (charge state, etc.) and will not require Shift to be held down.")
                .define("Always Show Item Details", alwaysShowDetails);

        clientHoldShiftForDetails = CLIENT_CONFIG
                .comment("If TRUE, CoFH Items will display a message prompting to hold Shift to see full details (charge state, etc.). This does not change the behavior, only if the informational message should display.")
                .define("Show 'Hold Shift for Details' Message", holdShiftForDetails);

        CLIENT_CONFIG.pop();

        clientSpec = CLIENT_CONFIG.build();

        refreshClientConfig();
    }

    private static void genEnchantmentConfig() {

        String treasure = "This sets whether or not the Enchantment is considered a 'treasure' enchantment.";
        String level = "This option adjusts the maximum allowable level for the Enchantment.";

        SERVER_CONFIG.push("Enchantments");

        SERVER_CONFIG.push("Holding");
        enableHolding = SERVER_CONFIG
                .comment("If TRUE, the Holding Enchantment is available for various Storage Items and Blocks.")
                .define("Enable", true);
        treasureHolding = SERVER_CONFIG
                .comment(treasure)
                .define("Treasure", false);
        levelHolding = SERVER_CONFIG
                .comment(level)
                .defineInRange("Max Level", 4, 1, MAX_ENCHANT_LEVEL);
        SERVER_CONFIG.pop();

        SERVER_CONFIG.pop();
    }

    private static void refreshServerConfig() {

        SubCommandCrafting.permissionLevel = permissionCrafting.get();
        SubCommandEnderChest.permissionLevel = permissionEnderChest.get();
        SubCommandHeal.permissionLevel = permissionHeal.get();
        SubCommandIgnite.permissionLevel = permissionIgnite.get();
        SubCommandRepair.permissionLevel = permissionRepair.get();

        improvedFeatherFalling = serverImprovedFeatherFalling.get();
        improvedMending = serverImprovedMending.get();

        enableFishingExhaustion = serverEnableFishingExhaustion.get();
        amountFishingExhaustion = serverAmountFishingExhaustion.get().floatValue();

        enableSaplingGrowthMod = serverEnableSaplingGrowthMod.get();
        amountSaplingGrowthMod = serverAmountSaplingGrowthMod.get();
    }

    private static void refreshClientConfig() {

        enableEnchantmentDescriptions = clientEnableEnchantmentDescriptions.get();
        enableItemDescriptions = clientEnableItemDescriptions.get();
        enableItemTags = clientEnableItemTags.get();

        alwaysShowDetails = clientAlwaysShowDetails.get();
        holdShiftForDetails = clientHoldShiftForDetails.get();
    }

    private static void refreshEnchantmentConfig() {

        if (HOLDING instanceof EnchantmentCoFH) {
            ((EnchantmentCoFH) HOLDING).setEnable(enableHolding.get());
            ((EnchantmentCoFH) HOLDING).setTreasureEnchantment(treasureHolding.get());
            ((EnchantmentCoFH) HOLDING).setMaxLevel(levelHolding.get());
        }
    }
    // endregion

    // region VARIABLES
    public static IntValue permissionCrafting;
    public static IntValue permissionEnderChest;
    public static IntValue permissionHeal;
    public static IntValue permissionIgnite;
    public static IntValue permissionRepair;

    public static boolean improvedFeatherFalling = true;
    public static boolean improvedMending = true;

    public static boolean enableFishingExhaustion = false;
    public static float amountFishingExhaustion = 0.125F;

    public static boolean enableSaplingGrowthMod = false;
    public static int amountSaplingGrowthMod = 4;

    public static boolean enableEnchantmentDescriptions = true;
    public static boolean enableItemDescriptions = true;
    public static boolean enableItemTags = true;

    public static boolean alwaysShowDetails = false;
    public static boolean holdShiftForDetails = true;

    private static BooleanValue serverImprovedFeatherFalling;
    private static BooleanValue serverImprovedMending;

    private static BooleanValue serverEnableFishingExhaustion;
    private static DoubleValue serverAmountFishingExhaustion;

    private static BooleanValue serverEnableSaplingGrowthMod;
    private static IntValue serverAmountSaplingGrowthMod;

    private static BooleanValue clientEnableEnchantmentDescriptions;
    private static BooleanValue clientEnableItemDescriptions;
    private static BooleanValue clientEnableItemTags;

    private static BooleanValue clientAlwaysShowDetails;
    private static BooleanValue clientHoldShiftForDetails;

    private static BooleanValue enableHolding;
    private static BooleanValue treasureHolding;
    private static IntValue levelHolding;
    // endregion

    // region CONFIGURATION
    @SubscribeEvent
    public static void configLoading(final ModConfig.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public static void configReloading(final ModConfig.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
