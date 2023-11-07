package cofh.core.common.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;

import static cofh.core.common.command.CoFHCommand.CMD_TARGETS;
import static cofh.core.init.CoreMobEffects.SUPERCHARGE;

public class SubCommandRecharge {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("recharge")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> chargeEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> chargeEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int chargeEntities(CommandSourceStack source, Collection<? extends ServerPlayer> targets) {

        for (ServerPlayer entity : targets) {
            entity.addEffect(new MobEffectInstance(SUPERCHARGE.get(), 1200, 0, false, false));
        }
        if (targets.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.cofh.recharge.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.cofh.recharge.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
