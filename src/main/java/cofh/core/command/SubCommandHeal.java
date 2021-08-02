package cofh.core.command;

import cofh.core.potion.PanaceaEffect;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_TARGETS;
import static cofh.lib.util.constants.Constants.MAX_FOOD_LEVEL;

public class SubCommandHeal {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("heal")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                // Self
                .executes(context -> healEntities(context.getSource(), ImmutableList.of(context.getSource().asPlayer())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> healEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int healEntities(CommandSource source, Collection<? extends ServerPlayerEntity> targets) {

        for (ServerPlayerEntity entity : targets) {
            // Extinguish Fire
            entity.extinguish();
            // Clear all negative effects
            PanaceaEffect.clearHarmfulEffects(entity);
            // Set to Max Air
            entity.setAir(entity.getMaxAir());
            // Set to Max Food
            entity.getFoodStats().addStats(MAX_FOOD_LEVEL, 5.0F);
            // Heal to Max Health
            entity.setHealth(entity.getMaxHealth());
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.heal.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.heal.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
