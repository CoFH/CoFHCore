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
                .requires(source -> source.hasPermission(permissionLevel))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    static ArgumentBuilder<CommandSource, ?> registerAlt() {

        return Commands.literal("workbench")
                .requires(source -> source.hasPermission(permissionLevel))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    private static int openContainer(PlayerEntity playerEntity) {

        playerEntity.openMenu(new SimpleNamedContainerProvider((id, inventory, player) -> new WorkbenchContainer(id, inventory) {

            @Override
            public boolean stillValid(PlayerEntity playerIn) {

                return true;
            }
        }, TITLE));
        return 1;
    }

}
