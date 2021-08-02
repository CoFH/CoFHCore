package cofh.core.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class SubCommandTPX {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("tpx")
                .requires(source -> source.hasPermissionLevel(permissionLevel));
        // No target - self
        //.executes(context -> execute(context.getSource(), context.getSource().asPlayer()));
    }

    static ArgumentBuilder<CommandSource, ?> registerAlt() {

        return Commands.literal("teleport")
                .requires(source -> source.hasPermissionLevel(permissionLevel));
        // No target - self
        //.executes(context -> execute(context.getSource(), context.getSource().asPlayer()));
    }

    private static int execute(CommandSource source, Collection<? extends Entity> targets) {

        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.tpx.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.cofh.tpx.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}
