package cofh.core.config;

import cofh.lib.config.IBaseConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoreClientConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Tooltips");

        cfgEnableEnchantmentDescriptions = builder
                .comment("If TRUE, Enchantment descriptions will be added to the tooltip for Enchanted Books containing only a single enchantment.")
                .define("Show Enchantment Descriptions", enableEnchantmentDescriptions);

        //        clientEnableFoodDescriptions = builder
        //                .comment("If TRUE, Food descriptions (nutrition and effects) will be added to the tooltip for Food items.")
        //                .define("Show Food Descriptions", enableFoodDescriptions);

        cfgEnableItemDescriptions = builder
                .comment("If TRUE, Item descriptions will be added to their tooltips if possible.")
                .define("Show Item Descriptions", enableItemDescriptions);

        cfgEnableItemTags = builder
                .comment("If TRUE and Advanced Tooltips are enabled (F3+H), Tags will be will be added to item tooltips if possible.")
                .define("Show Item Tags", enableItemTags);

        cfgEnableKeywords = builder
                .comment("If TRUE, Items will be associated with various keywords which assist with searching in various menus such as JEI.")
                .define("Show Item Descriptions", enableKeywords);

        cfgAlwaysShowDetails = builder
                .comment("If TRUE, CoFH Items will always show full details (charge state, etc.) and will not require Shift to be held down.")
                .define("Always Show Item Details", alwaysShowDetails);

        cfgHoldShiftForDetails = builder
                .comment("If TRUE, CoFH Items will display a message prompting to hold Shift to see full details (charge state, etc.). This does not change the behavior, only if the informational message should display.")
                .define("Show 'Hold Shift for Details' Message", holdShiftForDetails);

        builder.pop();
    }

    @Override
    public void refresh() {

        enableEnchantmentDescriptions = cfgEnableEnchantmentDescriptions.get();
        // enableFoodDescriptions = clientEnableFoodDescriptions.get();
        enableItemDescriptions = cfgEnableItemDescriptions.get();
        enableItemTags = cfgEnableItemTags.get();
        enableKeywords = cfgEnableKeywords.get();

        alwaysShowDetails = cfgAlwaysShowDetails.get();
        holdShiftForDetails = cfgHoldShiftForDetails.get();
    }

    // region VARIABLES
    public static boolean enableEnchantmentDescriptions = true;
    public static boolean enableFoodDescriptions = true;
    public static boolean enableItemDescriptions = true;
    public static boolean enableItemTags = true;
    public static boolean enableKeywords = true;

    public static boolean alwaysShowDetails = false;
    public static boolean holdShiftForDetails = true;

    private ForgeConfigSpec.BooleanValue cfgEnableEnchantmentDescriptions;
    private ForgeConfigSpec.BooleanValue cfgEnableFoodDescriptions;
    private ForgeConfigSpec.BooleanValue cfgEnableItemDescriptions;
    private ForgeConfigSpec.BooleanValue cfgEnableItemTags;
    private ForgeConfigSpec.BooleanValue cfgEnableKeywords;

    private ForgeConfigSpec.BooleanValue cfgAlwaysShowDetails;
    private ForgeConfigSpec.BooleanValue cfgHoldShiftForDetails;
    // endregion
}
