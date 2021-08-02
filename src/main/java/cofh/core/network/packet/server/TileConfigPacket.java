package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.tileentity.TileCoFH;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.tileentity.ITilePacketHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_CONFIG;

public class TileConfigPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected PacketBuffer buffer;

    public TileConfigPacket() {

        super(PACKET_CONFIG, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITilePacketHandler) {
            ((ITilePacketHandler) tile).handleConfigPacket(buffer);
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
        buf.writeBytes(buffer);
    }

    @Override
    public void read(PacketBuffer buf) {

        buffer = buf;
        pos = buffer.readBlockPos();
    }

    public static void sendToServer(TileCoFH tile) {

        TileConfigPacket packet = new TileConfigPacket();
        packet.pos = tile.pos();
        packet.buffer = tile.getConfigPacket(new PacketBuffer(Unpooled.buffer()));
        packet.sendToServer();
    }

}
