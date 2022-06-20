package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import cofh.lib.util.control.IReconfigurable.SideConfig;
import cofh.core.util.control.IReconfigurableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.lib.util.Constants.PACKET_SIDE_CONFIG;
import static cofh.lib.util.control.IReconfigurable.SideConfig.SIDE_NONE;

public class SideConfigPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected SideConfig[] sides = {SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};

    public SideConfigPacket() {

        super(PACKET_SIDE_CONFIG, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IReconfigurableTile) {
            ((IReconfigurableTile) tile).reconfigControl().setSideConfig(sides);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);

        byte[] bSides = new byte[6];
        for (int i = 0; i < 6; ++i) {
            bSides[i] = (byte) sides[i].ordinal();
        }
        buf.writeByteArray(bSides);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();

        byte[] bSides = buf.readByteArray(6);
        if (bSides.length == 6) {
            for (int i = 0; i < 6; ++i) {
                if (bSides[i] > SideConfig.VALUES.length) {
                    bSides[i] = 0;
                }
                sides[i] = SideConfig.VALUES[bSides[i]];
            }
        }
    }

    public static void sendToServer(IReconfigurableTile tile) {

        SideConfigPacket packet = new SideConfigPacket();
        packet.pos = tile.pos();
        packet.sides = tile.reconfigControl().getSideConfig();
        packet.sendToServer();
    }

}
