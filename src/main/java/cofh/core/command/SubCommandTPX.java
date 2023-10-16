package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class SubCommandTPX {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("tpx")
                .requires(source -> source.hasPermission(permissionLevel));
        // No target - self
        //.executes(context -> execute(context.getSource(), context.getSource().asPlayer()));
    }

    static ArgumentBuilder<CommandSourceStack, ?> registerAlt() {

        return Commands.literal("teleport")
                .requires(source -> source.hasPermission(permissionLevel));
        // No target - self
        //.executes(context -> execute(context.getSource(), context.getSource().asPlayer()));
    }

    private static int execute(CommandSourceStack source, Collection<? extends Entity> targets) {

        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cofh.tpx.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cofh.tpx.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
