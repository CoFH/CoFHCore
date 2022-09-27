package cofh.core.config;

import cofh.core.command.*;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoreCommandConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Commands");

        permissionCrafting = builder
                .comment("The required permission level for the '/cofh crafting' command.")
                .defineInRange("Crafting Permission Level", SubCommandCrafting.permissionLevel, 0, 4);
        permissionEnderChest = builder
                .comment("The required permission level for the '/cofh enderchest' command.")
                .defineInRange("EnderChest Permission Level", SubCommandEnderChest.permissionLevel, 0, 4);
        permissionHeal = builder
                .comment("The required permission level for the '/cofh heal' command.")
                .defineInRange("Heal Permission Level", SubCommandHeal.permissionLevel, 0, 4);
        permissionIgnite = builder
                .comment("The required permission level for the '/cofh ignite' command.")
                .defineInRange("Ignite Permission Level", SubCommandIgnite.permissionLevel, 0, 4);
        permissionRepair = builder
                .comment("The required permission level for the '/cofh repair' command.")
                .defineInRange("Repair Permission Level", SubCommandRepair.permissionLevel, 0, 4);

        builder.pop();
    }

    @Override
    public void refresh() {

        SubCommandCrafting.permissionLevel = permissionCrafting.get();
        SubCommandEnderChest.permissionLevel = permissionEnderChest.get();
        SubCommandHeal.permissionLevel = permissionHeal.get();
        SubCommandIgnite.permissionLevel = permissionIgnite.get();
        SubCommandRepair.permissionLevel = permissionRepair.get();
    }

    // region VARIABLES
    public static ForgeConfigSpec.IntValue permissionCrafting;
    public static ForgeConfigSpec.IntValue permissionEnderChest;
    public static ForgeConfigSpec.IntValue permissionHeal;
    public static ForgeConfigSpec.IntValue permissionIgnite;
    public static ForgeConfigSpec.IntValue permissionRepair;
    // endregion
}
