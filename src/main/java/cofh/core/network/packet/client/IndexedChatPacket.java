package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import static cofh.lib.util.constants.Constants.PACKET_CHAT;

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
    public void write(PacketBuffer buf) {

        buf.writeInt(index);
        buf.writeString(message);
    }

    @Override
    public void read(PacketBuffer buf) {

        index = buf.readInt();
        message = buf.readString(Short.MAX_VALUE);
    }

    public static void sendToClient(ITextComponent chat, int index, ServerPlayerEntity player) {

        IndexedChatPacket packet = new IndexedChatPacket();
        packet.index = index;
        packet.message = StringHelper.toJSON(chat);
        packet.sendToPlayer(player);
    }

}
