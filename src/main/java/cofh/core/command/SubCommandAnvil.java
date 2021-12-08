package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;

public class SubCommandAnvil {

    public static int permissionLevel = 2;

    static final TranslationTextComponent TITLE = new TranslationTextComponent("container.repair");

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("anvil")
                .requires(source -> source.hasPermission(permissionLevel))
                .executes(context -> openContainer(context.getSource().getPlayerOrException()));
    }

    private static int openContainer(PlayerEntity playerEntity) {

        playerEntity.openMenu(new SimpleNamedContainerProvider((id, player, inv) -> new RepairContainer(id, player) {

            @Override
            public boolean stillValid(PlayerEntity playerIn) {

                return true;
            }
        }, TITLE));
        return 1;
    }

}
