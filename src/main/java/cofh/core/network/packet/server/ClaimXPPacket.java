package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.block.entity.ITileXpHandler;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import static cofh.lib.util.Constants.PACKET_CLAIM_XP;

public class ClaimXPPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected Vec3 spawnPos;

    public ClaimXPPacket() {

        super(PACKET_CLAIM_XP, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ITileXpHandler) {
            ((ITileXpHandler) tile).claimXP(player);
        }
        // TODO: Debug logging?
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
    }

    public static boolean sendToServer(ITileXpHandler tile) {

        if (tile == null) {
            return false;
        }
        ClaimXPPacket packet = new ClaimXPPacket();
        packet.pos = tile.pos();
        packet.sendToServer();
        return true;
    }

}
