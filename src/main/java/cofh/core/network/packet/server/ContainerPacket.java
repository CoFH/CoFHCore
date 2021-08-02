package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.lib.inventory.container.ContainerCoFH;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import static cofh.lib.util.constants.Constants.PACKET_CONTAINER;

public class ContainerPacket extends PacketBase implements IPacketServer {

    protected PacketBuffer buffer;

    public ContainerPacket() {

        super(PACKET_CONTAINER, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        if (player.openContainer instanceof ContainerCoFH) {
            ((ContainerCoFH) player.openContainer).handleContainerPacket(buffer);
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBytes(buffer);
    }

    @Override
    public void read(PacketBuffer buf) {

        buffer = buf;
    }

    public static void sendToServer(ContainerCoFH container) {

        ContainerPacket packet = new ContainerPacket();
        packet.buffer = container.getContainerPacket(new PacketBuffer(Unpooled.buffer()));
        packet.sendToServer();
    }

}
