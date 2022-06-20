package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import cofh.lib.util.control.ISecurable;
import cofh.lib.util.control.ISecurable.AccessMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.lib.util.Constants.PACKET_SECURITY;

public class SecurityPacket extends PacketBase implements IPacketServer {

    protected byte mode;

    public SecurityPacket() {

        super(PACKET_SECURITY, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (player.containerMenu instanceof ISecurable) {
            ((ISecurable) player.containerMenu).setAccess(AccessMode.VALUES[mode]);
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
