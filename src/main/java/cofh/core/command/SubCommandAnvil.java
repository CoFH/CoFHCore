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
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .executes(context -> openContainer(context.getSource().asPlayer()));
    }

    private static int openContainer(PlayerEntity playerEntity) {

        playerEntity.openContainer(new SimpleNamedContainerProvider((id, player, inv) -> new RepairContainer(id, player), TITLE));
        return 1;
    }

}
