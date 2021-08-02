package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.tileentity.TileCoFH;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.tileentity.ITilePacketHandler;
import cofh.lib.util.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.NETWORK_UPDATE_DISTANCE;
import static cofh.lib.util.constants.Constants.PACKET_CONTROL;

public class TileControlPacket extends PacketBase implements IPacketClient {

    protected BlockPos pos;
    protected PacketBuffer buffer;

    public TileControlPacket() {

        super(PACKET_CONTROL, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        World world = ProxyUtils.getClientWorld();
        if (world == null) {
            CoFHCore.LOG.error("Client world is null! (Is this being called on the server?)");
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ITilePacketHandler) {
            ((ITilePacketHandler) tile).handleControlPacket(buffer);
            BlockState state = tile.getWorld().getBlockState(pos);
            tile.getWorld().notifyBlockUpdate(pos, state, state, 3);
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

    public static void sendToClient(TileCoFH tile) {

        if (tile.world() == null || Utils.isClientWorld(tile.world())) {
            return;
        }
        TileControlPacket packet = new TileControlPacket();
        packet.pos = tile.pos();
        packet.buffer = tile.getControlPacket(new PacketBuffer(Unpooled.buffer()));
        packet.sendToAllAround(packet.pos, NETWORK_UPDATE_DISTANCE, tile.world().getDimensionKey());
    }

}
