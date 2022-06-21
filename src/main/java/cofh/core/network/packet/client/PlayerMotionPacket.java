package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import static cofh.core.network.packet.PacketIDs.PACKET_MOTION;

public class PlayerMotionPacket extends PacketBase implements IPacketClient {

    protected double motionX;
    protected double motionY;
    protected double motionZ;

    public PlayerMotionPacket() {

        super(PACKET_MOTION, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProxyUtils.getClientPlayer().setDeltaMovement(ProxyUtils.getClientPlayer().getDeltaMovement().add(motionX, motionY, motionZ));
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        motionX = buf.readDouble();
        motionY = buf.readDouble();
        motionZ = buf.readDouble();
    }

    public static void sendToClient(double x, double y, double z, ServerPlayer player) {

        PlayerMotionPacket packet = new PlayerMotionPacket();
        packet.motionX = x;
        packet.motionY = y;
        packet.motionZ = z;
        packet.sendToPlayer(player);
    }

}
