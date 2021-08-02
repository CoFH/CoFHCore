package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.control.IReconfigurableTile;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.control.IReconfigurable.SideConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_SIDE_CONFIG;
import static cofh.lib.util.control.IReconfigurable.SideConfig.SIDE_NONE;

public class SideConfigPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected SideConfig[] sides = {SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};

    public SideConfigPacket() {

        super(PACKET_SIDE_CONFIG, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IReconfigurableTile) {
            ((IReconfigurableTile) tile).reconfigControl().setSideConfig(sides);
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);

        byte[] bSides = new byte[6];
        for (int i = 0; i < 6; ++i) {
            bSides[i] = (byte) sides[i].ordinal();
        }
        buf.writeByteArray(bSides);
    }

    @Override
    public void read(PacketBuffer buf) {

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
