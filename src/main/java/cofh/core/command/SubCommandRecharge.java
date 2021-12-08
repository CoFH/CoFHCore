package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_TARGETS;
import static cofh.lib.util.references.CoreReferences.SUPERCHARGE;

public class SubCommandRecharge {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("recharge")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> chargeEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> chargeEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int chargeEntities(CommandSource source, Collection<? extends ServerPlayerEntity> targets) {

        for (ServerPlayerEntity entity : targets) {
            entity.addEffect(new EffectInstance(SUPERCHARGE, 1200, 0, false, false));
        }
        if (targets.size() == 1) {
            source.sendSuccess(new TranslationTextComponent("commands.cofh.recharge.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.cofh.recharge.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
