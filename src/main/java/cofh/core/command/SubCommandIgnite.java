package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_DURATION;
import static cofh.lib.util.constants.Constants.CMD_TARGETS;

public class SubCommandIgnite {

    public static int permissionLevel = 2;

    static final int DEFAULT_DURATION = 10;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("ignite")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self - default duration
                .executes(context -> igniteEntities(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException()), DEFAULT_DURATION))
                // Duration specified
                .then(Commands.argument(CMD_DURATION, IntegerArgumentType.integer())
                        .executes(context -> igniteEntities(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException()), IntegerArgumentType.getInteger(context, CMD_DURATION))))
                // Targets specified - default duration
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .executes(context -> igniteEntities(context.getSource(), EntityArgument.getEntities(context, CMD_TARGETS), DEFAULT_DURATION)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_DURATION, IntegerArgumentType.integer())
                                .executes(context -> igniteEntities(context.getSource(), EntityArgument.getEntities(context, CMD_TARGETS), IntegerArgumentType.getInteger(context, CMD_DURATION)))));
    }

    private static int igniteEntities(CommandSourceStack source, Collection<? extends Entity> targets, int duration) {

        for (Entity entity : targets) {
            entity.setSecondsOnFire(duration);
        }
        if (targets.size() == 1) {
            source.sendSuccess(Component.translatable("commands.cofh.ignite.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.cofh.ignite.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
