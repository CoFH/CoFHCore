package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;

public class SubCommandAnvil {

    public static int permissionLevel = 2;

    static final MutableComponent TITLE = Component.translatable("container.repair");

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("anvil")
                .requires(source -> source.hasPermission(permissionLevel))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    private static int openContainer(Player playerEntity) {

        playerEntity.openMenu(new SimpleMenuProvider((id, player, inv) -> new AnvilMenu(id, player) {

            @Override
            public boolean stillValid(Player playerIn) {

                return true;
            }
        }, TITLE));
        return 1;
    }

}
