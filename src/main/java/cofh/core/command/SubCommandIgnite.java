package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_DURATION;
import static cofh.lib.util.constants.Constants.CMD_TARGETS;

public class SubCommandIgnite {

    public static int permissionLevel = 2;

    static final int DEFAULT_DURATION = 10;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("ignite")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                // Self - default duration
                .executes(context -> igniteEntities(context.getSource(), ImmutableList.of(context.getSource().assertIsEntity()), DEFAULT_DURATION))
                // Duration specified
                .then(Commands.argument(CMD_DURATION, IntegerArgumentType.integer())
                        .executes(context -> igniteEntities(context.getSource(), ImmutableList.of(context.getSource().assertIsEntity()), IntegerArgumentType.getInteger(context, CMD_DURATION))))
                // Targets specified - default duration
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .executes(context -> igniteEntities(context.getSource(), EntityArgument.getEntities(context, CMD_TARGETS), DEFAULT_DURATION)))
                // Targets AND duration specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .then(Commands.argument(CMD_DURATION, IntegerArgumentType.integer())
                                .executes(context -> igniteEntities(context.getSource(), EntityArgument.getEntities(context, CMD_TARGETS), IntegerArgumentType.getInteger(context, CMD_DURATION)))));
    }

    private static int igniteEntities(CommandSource source, Collection<? extends Entity> targets, int duration) {

        for (Entity entity : targets) {
            entity.setFire(duration);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.ignite.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.ignite.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
