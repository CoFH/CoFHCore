package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.core.common.network.packet.PacketIDs.PACKET_CONFIG;

public class TileConfigPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected FriendlyByteBuf buffer;

    public TileConfigPacket() {

        super(PACKET_CONFIG, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IPacketHandlerTile handlerTile) {
            handlerTile.handleConfigPacket(buffer);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeBytes(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        buffer = buf;
        pos = buffer.readBlockPos();
    }

    public static void sendToServer(IPacketHandlerTile tile) {

        TileConfigPacket packet = new TileConfigPacket();
        packet.pos = tile.pos();
        packet.buffer = tile.getConfigPacket(new FriendlyByteBuf(Unpooled.buffer()));
        packet.sendToServer();
    }

}
