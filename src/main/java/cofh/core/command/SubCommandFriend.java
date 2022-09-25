package cofh.core.command;

import cofh.lib.util.SocialUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static cofh.lib.util.Constants.CMD_PLAYERS;

public class SubCommandFriend {

    public static int permissionLevel = 0;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("friend")
                .requires(source -> source.hasPermission(permissionLevel))
                .then(Commands.literal("add")
                        .then(Commands.argument(CMD_PLAYERS, GameProfileArgument.gameProfile())
                                .executes((context) -> addFriends(context.getSource().getPlayerOrException(), GameProfileArgument.getGameProfiles(context, CMD_PLAYERS)))))
                .then(Commands.literal("remove")
                        .then(Commands.argument(CMD_PLAYERS, GameProfileArgument.gameProfile())
                                .executes((context) -> removeFriends(context.getSource().getPlayerOrException(), GameProfileArgument.getGameProfiles(context, CMD_PLAYERS)))))
                .then(Commands.literal("clear")
                        .executes((context) -> clearFriends(context.getSource().getPlayerOrException())));
    }

    private static int addFriends(ServerPlayer user, Collection<GameProfile> players) {

        for (GameProfile player : players) {
            SocialUtils.addFriend(user, player);
        }
        return players.size();
    }

    private static int removeFriends(ServerPlayer user, Collection<GameProfile> players) {

        for (GameProfile player : players) {
            SocialUtils.removeFriend(user, player);
        }
        return players.size();
    }

    private static int clearFriends(ServerPlayer user) {

        return SocialUtils.clearFriendList(user) ? 1 : 0;
    }

}
