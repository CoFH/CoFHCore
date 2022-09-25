package cofh.core.command;

import cofh.lib.util.Utils;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collection;

import static cofh.lib.util.Constants.CMD_TARGETS;

public class SubCommandZap {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("zap")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> zapEntities(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.entities())
                        .executes(context -> zapEntities(context.getSource(), EntityArgument.getEntities(context, CMD_TARGETS))));
    }

    private static int zapEntities(CommandSourceStack source, Collection<? extends Entity> targets) {

        int zappedEntities = 0;
        ServerPlayer caster = source.getEntity() instanceof ServerPlayer player ? player : null;

        for (Entity entity : targets) {
            if (Utils.spawnLightningBolt(entity.level, entity.blockPosition(), caster)) {
                ++zappedEntities;
            }
        }
        if (targets.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.cofh.zap.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(new TranslatableComponent("commands.cofh.zap.success.multiple", targets.size()), true);
        }
        return zappedEntities;
    }

    public static boolean createLightningBolt(Level world, BlockPos pos, ServerPlayer caster) {

        if (world.canSeeSky(pos)) {
            Utils.spawnLightningBolt(world, pos, caster);
            return true;
        }
        return false;
    }

}
