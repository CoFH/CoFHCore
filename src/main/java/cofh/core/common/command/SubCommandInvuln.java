package cofh.core.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.function.Supplier;

import static cofh.core.common.command.CoFHCommand.CMD_FLAG;
import static cofh.core.common.command.CoFHCommand.CMD_TARGETS;

public class SubCommandInvuln {

    public static Supplier<Integer> permissionLevel = () -> 2;

    static final boolean DEFAULT_FLAG = true;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("invuln")
                .requires(source -> source.hasPermission(permissionLevel.get()))
                // Self
                .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException()), DEFAULT_FLAG))
                // Flag Specified
                .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                        .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException()), BoolArgumentType.getBool(context, CMD_FLAG))))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), DEFAULT_FLAG)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                                .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), BoolArgumentType.getBool(context, CMD_FLAG)))));
    }

    static ArgumentBuilder<CommandSourceStack, ?> registerAlt() {

        return Commands.literal("invulnerable")
                .requires(source -> source.hasPermission(permissionLevel.get()))
                // Self
                .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException()), DEFAULT_FLAG))
                // Flag Specified
                .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                        .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException()), BoolArgumentType.getBool(context, CMD_FLAG))))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), DEFAULT_FLAG)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                                .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), BoolArgumentType.getBool(context, CMD_FLAG)))));
    }

    private static int flagEntities(CommandSourceStack source, Collection<? extends ServerPlayer> targets, boolean flag) {

        for (ServerPlayer entity : targets) {
            entity.setInvulnerable(flag);
        }
        if (flag) {
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable("commands.cofh.invuln.success.single", targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendSuccess(() -> Component.translatable("commands.cofh.invuln.success.multiple", targets.size()), true);
            }
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(() -> Component.translatable("commands.cofh.invuln.remove.single", targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendSuccess(() -> Component.translatable("commands.cofh.invuln.remove.multiple", targets.size()), true);
            }
        }
        return targets.size();
    }

}
