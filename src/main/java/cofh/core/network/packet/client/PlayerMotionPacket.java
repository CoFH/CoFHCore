package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import static cofh.lib.util.constants.Constants.PACKET_MOTION;

public class PlayerMotionPacket extends PacketBase implements IPacketClient {

    protected double motionX;
    protected double motionY;
    protected double motionZ;

    public PlayerMotionPacket() {

        super(PACKET_MOTION, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ProxyUtils.getClientPlayer().setMotion(ProxyUtils.getClientPlayer().getMotion().add(motionX, motionY, motionZ));
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
    }

    @Override
    public void read(PacketBuffer buf) {

        motionX = buf.readDouble();
        motionY = buf.readDouble();
        motionZ = buf.readDouble();
    }

    public static void sendToClient(double x, double y, double z, ServerPlayerEntity player) {

        PlayerMotionPacket packet = new PlayerMotionPacket();
        packet.motionX = x;
        packet.motionY = y;
        packet.motionZ = z;
        packet.sendToPlayer(player);
    }

}
