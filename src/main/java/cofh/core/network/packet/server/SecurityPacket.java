package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.lib.api.control.ISecurable;
import cofh.lib.api.control.ISecurable.AccessMode;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.core.network.packet.PacketIDs.PACKET_SECURITY;

public class SecurityPacket extends PacketBase implements IPacketServer {

    protected byte mode;

    public SecurityPacket() {

        super(PACKET_SECURITY, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (player.containerMenu instanceof ISecurable securable) {
            securable.setAccess(AccessMode.VALUES[mode]);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeByte(mode);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        mode = buf.readByte();
    }

    public static void sendToServer(AccessMode accessMode) {

        SecurityPacket packet = new SecurityPacket();
        packet.mode = (byte) accessMode.ordinal();
        packet.sendToServer();
    }

}
