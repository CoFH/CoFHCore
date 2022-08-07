package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;

import java.util.function.Supplier;

public class SubCommandCrafting {

    public static Supplier<Integer> permissionLevel = () -> 2;

    static final MutableComponent TITLE = Component.literal("container.crafting");

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("crafting")
                .requires(source -> source.hasPermission(permissionLevel.get()))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> registerAlt() {

        return Commands.literal("workbench")
                .requires(source -> source.hasPermission(permissionLevel.get()))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    private static int openContainer(Player playerEntity) {

        playerEntity.openMenu(new SimpleMenuProvider((id, inventory, player) -> new CraftingMenu(id, inventory) {

            @Override
            public boolean stillValid(Player playerIn) {

                return true;
            }
        }, TITLE));
        return 1;
    }

}
