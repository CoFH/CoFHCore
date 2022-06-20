package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.network.packet.IPacketClient;
import cofh.core.network.packet.PacketBase;
import cofh.core.util.ProxyUtils;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static cofh.lib.util.Constants.PACKET_CHAT;

public class IndexedChatPacket extends PacketBase implements IPacketClient {

    protected int index;
    protected String message;

    public IndexedChatPacket() {

        super(PACKET_CHAT, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProxyUtils.addIndexedChatMessage(StringHelper.fromJSON(message), index);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(index);
        buf.writeUtf(message);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        index = buf.readInt();
        message = buf.readUtf(Short.MAX_VALUE);
    }

    public static void sendToClient(Component chat, int index, ServerPlayer player) {

        IndexedChatPacket packet = new IndexedChatPacket();
        packet.index = index;
        packet.message = StringHelper.toJSON(chat);
        packet.sendToPlayer(player);
    }

}
