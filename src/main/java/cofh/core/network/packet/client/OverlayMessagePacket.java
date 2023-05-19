package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static cofh.core.network.packet.PacketIDs.PACKET_OVERLAY;

public class OverlayMessagePacket extends PacketBase implements IPacketClient {

    protected String message;

    public OverlayMessagePacket() {

        super(PACKET_OVERLAY, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProxyUtils.setOverlayMessage(StringHelper.fromJSON(message));
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeUtf(message);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        message = buf.readUtf(Short.MAX_VALUE);
    }

    public static void sendToClient(Component message, ServerPlayer player) {

        OverlayMessagePacket packet = new OverlayMessagePacket();
        packet.message = StringHelper.toJSON(message);
        packet.sendToPlayer(player);
    }

}
