package cofh.core.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_TARGETS;

public class SubCommandCharge {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("charge")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                // Self
                .executes(context -> chargeEntityItems(context.getSource(), ImmutableList.of(context.getSource().asPlayer())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> chargeEntityItems(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int chargeEntityItems(CommandSource source, Collection<? extends ServerPlayerEntity> targets) {

        for (ServerPlayerEntity entity : targets) {
            // Main Inventory
            for (ItemStack stack : entity.inventory.mainInventory) {
                stack.getCapability(CapabilityEnergy.ENERGY, null)
                        .ifPresent(c -> c.receiveEnergy(Integer.MAX_VALUE, false));
            }
            // Armor Inventory
            for (ItemStack stack : entity.inventory.armorInventory) {
                stack.getCapability(CapabilityEnergy.ENERGY, null)
                        .ifPresent(c -> c.receiveEnergy(Integer.MAX_VALUE, false));
            }
            // Offhand
            for (ItemStack stack : entity.inventory.offHandInventory) {
                stack.getCapability(CapabilityEnergy.ENERGY, null)
                        .ifPresent(c -> c.receiveEnergy(Integer.MAX_VALUE, false));
            }
        }
        //        if (targets.size() == 1) {
        //            source.sendFeedback(new TranslationTextComponent("commands.cofh.charge.success.single", targets.iterator().next().getDisplayName()), true);
        //        } else {
        //            source.sendFeedback(new TranslationTextComponent("commands.cofh.charge.success.multiple", targets.size()), true);
        //        }
        return targets.size();
    }

}
