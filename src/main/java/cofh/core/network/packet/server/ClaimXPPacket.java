package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.tileentity.TileCoFH;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.PACKET_CLAIM_XP;

public class ClaimXPPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected Vector3d spawnPos;

    public ClaimXPPacket() {

        super(PACKET_CLAIM_XP, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        World world = player.world;
        if (!world.isBlockPresent(pos)) {
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileCoFH) {
            ((TileCoFH) tile).claimXP(player);
        }
        // TODO: Debug logging?
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public void read(PacketBuffer buf) {

        pos = buf.readBlockPos();
    }

    public static boolean sendToServer(TileCoFH tile) {

        if (tile == null) {
            return false;
        }
        ClaimXPPacket packet = new ClaimXPPacket();
        packet.pos = tile.pos();
        packet.sendToServer();
        return true;
    }

}
