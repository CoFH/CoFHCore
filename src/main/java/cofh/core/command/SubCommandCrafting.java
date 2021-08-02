package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.text.TranslationTextComponent;

public class SubCommandCrafting {

    public static int permissionLevel = 2;

    static final TranslationTextComponent TITLE = new TranslationTextComponent("container.crafting");

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("crafting")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .executes(context -> openContainer(context.getSource().asPlayer()));
    }

    static ArgumentBuilder<CommandSource, ?> registerAlt() {

        return Commands.literal("workbench")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .executes(context -> openContainer(context.getSource().asPlayer()));
    }

    private static int openContainer(PlayerEntity playerEntity) {

        playerEntity.openContainer(new SimpleNamedContainerProvider((id, player, inv) -> new WorkbenchContainer(id, player), TITLE));
        return 1;
    }

}
