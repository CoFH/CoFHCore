package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_TARGETS;

public class SubCommandRepair {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("repair")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> repairEquipment(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> repairEquipment(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int repairEquipment(CommandSource source, Collection<? extends ServerPlayerEntity> targets) {

        int repairedEquipment = 0;

        for (ServerPlayerEntity entity : targets) {
            for (ItemStack stack : entity.getAllSlots()) {
                if (stack.isDamageableItem() && stack.isDamaged()) {
                    stack.setDamageValue(0);
                    ++repairedEquipment;
                }
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(new TranslationTextComponent("commands.cofh.repair.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.cofh.repair.success.multiple", targets.size()), true);
        }
        return repairedEquipment;
    }

}
