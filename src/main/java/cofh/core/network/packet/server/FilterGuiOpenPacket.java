package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.filter.IFilterableTile;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.core.network.packet.PacketIDs.PACKET_GUI_OPEN;

public class FilterGuiOpenPacket extends PacketBase implements IPacketServer {

    public static byte TILE = 0;
    public static byte FILTER = 1;

    protected BlockPos pos;
    protected byte mode;
    protected byte guiId;

    public FilterGuiOpenPacket() {

        super(PACKET_GUI_OPEN, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IFilterableTile) {
            if (mode == TILE) {
                ((IFilterableTile) tile).openGui(player, guiId);
            } else if (mode == FILTER) {
                ((IFilterableTile) tile).openFilterGui(player, guiId);
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeByte(mode);
        buf.writeByte(guiId);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
        mode = buf.readByte();
    }

    public static void openFilterGui(IFilterableTile tile, byte filterId) {

        sendToServer(tile, FILTER, filterId);
    }

    public static void openTileGui(IFilterableTile tile, byte filterId) {

        sendToServer(tile, TILE, filterId);
    }

    protected static void sendToServer(IFilterableTile tile, byte mode, byte guiId) {

        FilterGuiOpenPacket packet = new FilterGuiOpenPacket();
        packet.pos = tile.pos();
        packet.mode = mode;
        packet.guiId = guiId;
        packet.sendToServer();
    }

}
