package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_FLAG;
import static cofh.lib.util.constants.Constants.CMD_TARGETS;

public class SubCommandInvis {

    public static int permissionLevel = 2;

    static final boolean DEFAULT_FLAG = true;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("invis")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                // Self
                .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().asPlayer()), DEFAULT_FLAG))
                // Flag Specified
                .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                        .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().asPlayer()), BoolArgumentType.getBool(context, CMD_FLAG))))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), DEFAULT_FLAG)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                                .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), BoolArgumentType.getBool(context, CMD_FLAG)))));
    }

    static ArgumentBuilder<CommandSource, ?> registerAlt() {

        return Commands.literal("invisible")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                // Self
                .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().asPlayer()), DEFAULT_FLAG))
                // Flag Specified
                .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                        .executes(context -> flagEntities(context.getSource(), ImmutableList.of(context.getSource().asPlayer()), BoolArgumentType.getBool(context, CMD_FLAG))))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), DEFAULT_FLAG)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_FLAG, BoolArgumentType.bool())
                                .executes(context -> flagEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS), BoolArgumentType.getBool(context, CMD_FLAG)))));
    }

    private static int flagEntities(CommandSource source, Collection<? extends ServerPlayerEntity> targets, boolean flag) {

        for (ServerPlayerEntity entity : targets) {
            entity.setInvisible(flag);
        }
        if (flag) {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslationTextComponent("commands.cofh.invis.success.single", targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.cofh.invis.success.multiple", targets.size()), true);
            }
        } else {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslationTextComponent("commands.cofh.invis.remove.single", targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslationTextComponent("commands.cofh.invis.remove.multiple", targets.size()), true);
            }
        }
        return targets.size();
    }

}
