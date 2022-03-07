package cofh.core.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public class CoFHCommand {

    private static CoFHCommand instance;

    public static void initialize(CommandDispatcher<CommandSourceStack> dispatcher) {

        instance = new CoFHCommand(dispatcher);
    }

    private CoFHCommand(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("cofh")
                // TODO: This works but there's just not a need.
                //                .then(SubCommandAnvil.register())
                .then(SubCommandCrafting.register())
                .then(SubCommandCrafting.registerAlt())
                .then(SubCommandEnderChest.register())
                .then(SubCommandFriend.register())
                .then(SubCommandHeal.register())
                .then(SubCommandIgnite.register())
                .then(SubCommandInvis.register())
                .then(SubCommandInvuln.register())
                .then(SubCommandRecharge.register())
                .then(SubCommandRepair.register())
                .then(SubCommandZap.register())
        );
    }

}
