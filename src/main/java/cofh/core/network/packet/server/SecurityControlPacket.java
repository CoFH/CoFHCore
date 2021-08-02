package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.control.ISecurableTile;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.control.ISecurable.AccessMode;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_SECURITY_CONTROL;

public class SecurityControlPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected byte mode;

    public SecurityControlPacket() {

        super(PACKET_SECURITY_CONTROL, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ISecurableTile) {
            ((ISecurableTile) tile).setAccess(AccessMode.VALUES[mode]);
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

    public static void sendToServer(ISecurableTile tile) {

        SecurityControlPacket packet = new SecurityControlPacket();
        packet.pos = tile.pos();
        packet.mode = (byte) tile.securityControl().getAccess().ordinal();
        packet.sendToServer();
    }

}
