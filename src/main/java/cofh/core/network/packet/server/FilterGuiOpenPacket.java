package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.filter.IFilterableTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_GUI_OPEN;

public class FilterGuiOpenPacket extends PacketBase implements IPacketServer {

    public static byte TILE = 0;
    public static byte FILTER = 1;

    protected BlockPos pos;
    protected byte mode;

    public FilterGuiOpenPacket() {

        super(PACKET_GUI_OPEN, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IFilterableTile) {
            if (mode == TILE) {
                ((IFilterableTile) tile).openGui(player);
            } else if (mode == FILTER) {
                ((IFilterableTile) tile).openFilterGui(player);
            }
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
        buf.writeByte(mode);
    }

    @Override
    public void read(PacketBuffer buf) {

        pos = buf.readBlockPos();
        mode = buf.readByte();
    }

    public static void openFilterGui(IFilterableTile tile) {

        sendToServer(tile, FILTER);
    }

    public static void openTileGui(IFilterableTile tile) {

        sendToServer(tile, TILE);
    }

    protected static void sendToServer(IFilterableTile tile, byte mode) {

        FilterGuiOpenPacket packet = new FilterGuiOpenPacket();
        packet.pos = tile.pos();
        packet.mode = mode;
        packet.sendToServer();
    }

}
