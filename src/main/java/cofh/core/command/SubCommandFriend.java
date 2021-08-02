package cofh.core.command;

import cofh.lib.util.SocialUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_PLAYERS;

public class SubCommandFriend {

    public static int permissionLevel = 0;

    static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("friend")
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .then(Commands.literal("add")
                        .then(Commands.argument(CMD_PLAYERS, GameProfileArgument.gameProfile())
                                .executes((context) -> addFriends(context.getSource().asPlayer(), GameProfileArgument.getGameProfiles(context, CMD_PLAYERS)))))
                .then(Commands.literal("remove")
                        .then(Commands.argument(CMD_PLAYERS, GameProfileArgument.gameProfile())
                                .executes((context) -> removeFriends(context.getSource().asPlayer(), GameProfileArgument.getGameProfiles(context, CMD_PLAYERS)))))
                .then(Commands.literal("clear")
                        .executes((context) -> clearFriends(context.getSource().asPlayer())));
    }

    private static int addFriends(ServerPlayerEntity user, Collection<GameProfile> players) {

        for (GameProfile player : players) {
            SocialUtils.addFriend(user, player);
        }
        return players.size();
    }

    private static int removeFriends(ServerPlayerEntity user, Collection<GameProfile> players) {

        for (GameProfile player : players) {
            SocialUtils.removeFriend(user, player);
        }
        return players.size();
    }

    private static int clearFriends(ServerPlayerEntity user) {

        return SocialUtils.clearFriendList(user) ? 1 : 0;
    }

}
