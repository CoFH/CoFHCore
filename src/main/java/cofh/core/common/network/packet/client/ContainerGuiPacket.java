package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.common.inventory.ContainerMenuCoFH;
import cofh.core.util.ProxyUtils;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static cofh.core.common.network.packet.PacketIDs.PACKET_CONTAINER_GUI;

public class ContainerGuiPacket extends PacketBase implements IPacketClient {

    protected FriendlyByteBuf buffer;

    public ContainerGuiPacket() {

        super(PACKET_CONTAINER_GUI, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Player player = ProxyUtils.getClientPlayer();
        if (player.containerMenu instanceof ContainerMenuCoFH container) {
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

    public static void sendToClient(ContainerMenuCoFH container, Player player) {

        if (container != null && player instanceof ServerPlayer serverPlayer) {
            ContainerGuiPacket packet = new ContainerGuiPacket();
            packet.buffer = container.getGuiPacket(new FriendlyByteBuf(Unpooled.buffer()));
            packet.sendToPlayer(serverPlayer);
        }
    }

}
