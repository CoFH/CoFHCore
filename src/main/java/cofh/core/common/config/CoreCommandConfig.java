package cofh.core.common.config;

import cofh.core.common.command.*;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoreCommandConfig implements IBaseConfig {

    @Override
    public void apply(ForgeConfigSpec.Builder builder) {

        builder.push("Commands");

        SubCommandCrafting.permissionLevel = builder
                .comment("The required permission level for the '/cofh crafting' command.")
                .defineInRange("Crafting Permission Level", SubCommandCrafting.permissionLevel, 0, 4);
        SubCommandEnderChest.permissionLevel = builder
                .comment("The required permission level for the '/cofh enderchest' command.")
                .defineInRange("EnderChest Permission Level", SubCommandEnderChest.permissionLevel, 0, 4);
        SubCommandHeal.permissionLevel = builder
                .comment("The required permission level for the '/cofh heal' command.")
                .defineInRange("Heal Permission Level", SubCommandHeal.permissionLevel, 0, 4);
        SubCommandIgnite.permissionLevel = builder
                .comment("The required permission level for the '/cofh ignite' command.")
                .defineInRange("Ignite Permission Level", SubCommandIgnite.permissionLevel, 0, 4);
        SubCommandRepair.permissionLevel = builder
                .comment("The required permission level for the '/cofh repair' command.")
                .defineInRange("Repair Permission Level", SubCommandRepair.permissionLevel, 0, 4);

        builder.pop();
    }

}
