package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.inventory.container.ContainerCoFH;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.lib.util.Constants.PACKET_CONTAINER;

public class ContainerPacket extends PacketBase implements IPacketServer {

    protected FriendlyByteBuf buffer;

    public ContainerPacket() {

        super(PACKET_CONTAINER, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (player.containerMenu instanceof ContainerCoFH) {
            ((ContainerCoFH) player.containerMenu).handleContainerPacket(buffer);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBytes(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        buffer = buf;
    }

    public static void sendToServer(ContainerCoFH container) {

        ContainerPacket packet = new ContainerPacket();
        packet.buffer = container.getContainerPacket(new FriendlyByteBuf(Unpooled.buffer()));
        packet.sendToServer();
    }

}
