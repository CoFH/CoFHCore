package cofh.core.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class CoreClientConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Tooltips");

        enableEnchantmentDescriptions = builder
                .comment("If TRUE, Enchantment descriptions will be added to the tooltip for Enchanted Books containing only a single enchantment.")
                .define("Show Enchantment Descriptions", enableEnchantmentDescriptions);

        //        clientEnableFoodDescriptions = builder
        //                .comment("If TRUE, Food descriptions (nutrition and effects) will be added to the tooltip for Food items.")
        //                .define("Show Food Descriptions", enableFoodDescriptions);

        enableItemDescriptions = builder
                .comment("If TRUE, Item descriptions will be added to their tooltips if possible.")
                .define("Show Item Descriptions", enableItemDescriptions);

        enableItemTags = builder
                .comment("If TRUE and Advanced Tooltips are enabled (F3+H), Tags will be will be added to item tooltips if possible.")
                .define("Show Item Tags", enableItemTags);

        enableKeywords = builder
                .comment("If TRUE, Items will be associated with various keywords which assist with searching in various menus such as JEI.")
                .define("Show Item Descriptions", enableKeywords);

        alwaysShowDetails = builder
                .comment("If TRUE, CoFH Items will always show full details (charge state, etc.) and will not require Shift to be held down.")
                .define("Always Show Item Details", alwaysShowDetails);

        holdShiftForDetails = builder
                .comment("If TRUE, CoFH Items will display a message prompting to hold Shift to see full details (charge state, etc.). This does not change the behavior, only if the informational message should display.")
                .define("Show 'Hold Shift for Details' Message", holdShiftForDetails);

        particleDynamicLighting = builder
                .comment("If TRUE, certain CoFH particles will dynamically emit light.")
                .define("Dynamically Light Particles", particleDynamicLighting);

        stylizedGraphics = builder
                .comment("If TRUE, certain particles will use a stylized graphics pipeline on Fabulous graphics.")
                .define("Stylized Graphics", stylizedGraphics);

        builder.pop();
    }

    public static Supplier<Boolean> enableEnchantmentDescriptions = TRUE;
    public static Supplier<Boolean> enableFoodDescriptions = TRUE;
    public static Supplier<Boolean> enableItemDescriptions = TRUE;
    public static Supplier<Boolean> enableItemTags = TRUE;
    public static Supplier<Boolean> enableKeywords = TRUE;
    public static Supplier<Boolean> alwaysShowDetails = FALSE;
    public static Supplier<Boolean> holdShiftForDetails = TRUE;
    public static Supplier<Boolean> particleDynamicLighting = TRUE;
    public static Supplier<Boolean> stylizedGraphics = TRUE;

}
