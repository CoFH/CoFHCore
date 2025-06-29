package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.helpers.LightHelper;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import static cofh.core.common.network.packet.PacketIDs.PACKET_LIGHT_ADD;

public class LightAddPacket extends PacketBase implements IPacketClient {

    protected long pos;
    protected double radius;

    public LightAddPacket() {

        super(PACKET_LIGHT_ADD, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            LightHelper.addLight(level, BlockPos.of(pos), radius);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeLong(pos);
        buf.writeDouble(radius);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.pos = buf.readLong();
        this.radius = buf.readDouble();
    }

    public static void sendToClient(Level level, BlockPos pos, double radius) {

        if (!level.isClientSide) {
            LightAddPacket packet = new LightAddPacket();
            packet.pos = pos.asLong();
            packet.radius = radius;
            packet.sendToDimension(level.dimension());
        }
    }

    public static void sendToClient(ServerPlayer player, BlockPos pos, double radius) {

        LightAddPacket packet = new LightAddPacket();
        packet.pos = pos.asLong();
        packet.radius = radius;
        packet.sendToPlayer(player);
    }

}
