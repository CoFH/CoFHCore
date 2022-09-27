package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.inventory.container.ContainerCoFH;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static cofh.core.network.packet.PacketIDs.PACKET_CONTAINER_GUI;

public class ContainerGuiPacket extends PacketBase implements IPacketClient {

    protected FriendlyByteBuf buffer;

    public ContainerGuiPacket() {

        super(PACKET_CONTAINER_GUI, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Player player = ProxyUtils.getClientPlayer();
        if (player.containerMenu instanceof ContainerCoFH container) {
            container.handleGuiPacket(buffer);
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

    public static void sendToClient(ContainerCoFH container, Player player) {

        if (container != null && player instanceof ServerPlayer serverPlayer) {
            ContainerGuiPacket packet = new ContainerGuiPacket();
            packet.buffer = container.getGuiPacket(new FriendlyByteBuf(Unpooled.buffer()));
            packet.sendToPlayer(serverPlayer);
        }
    }

}
