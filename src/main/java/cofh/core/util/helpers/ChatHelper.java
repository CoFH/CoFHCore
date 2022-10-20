package cofh.core.util.helpers;

import cofh.core.network.packet.client.IndexedChatPacket;
import cofh.core.util.ProxyUtils;
import cofh.lib.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;

import java.util.List;

public final class ChatHelper {

    public static final int TEMP_INDEX_SERVER = -661464083; // Random Integer
    public static final int TEMP_INDEX_CLIENT = -1245781222; // Random Integer

    private static final boolean indexChatMessages = true;

    private ChatHelper() {

    }

    public static void sendIndexedChatMessageToPlayer(Player player, Component message) {

        if (player.level == null || Utils.isFakePlayer(player)) {
            return;
        }
        if (indexChatMessages) {
            if (Utils.isServerWorld(player.level)) {
                if (player instanceof ServerPlayer) {
                    IndexedChatPacket.sendToClient(message, TEMP_INDEX_SERVER, (ServerPlayer) player);
                }
            } else {
                ProxyUtils.addIndexedChatMessage(message, TEMP_INDEX_CLIENT);
            }
        } else {
            player.sendSystemMessage(message);
        }
    }

    public static void sendIndexedChatMessagesToPlayer(Player player, List<Component> messages) {

        if (player.level == null || Utils.isFakePlayer(player)) {
            return;
        }
        if (indexChatMessages) {
            for (int i = 0; i < messages.size(); ++i) {
                if (Utils.isServerWorld(player.level)) {
                    if (player instanceof ServerPlayer) {
                        IndexedChatPacket.sendToClient(messages.get(i), TEMP_INDEX_SERVER + i, (ServerPlayer) player);
                    }
                } else {
                    ProxyUtils.addIndexedChatMessage(messages.get(i), TEMP_INDEX_CLIENT + i);
                }
            }
        } else {
            for (Component message : messages) {
                player.sendSystemMessage(message);
            }
        }
    }

}
