package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.network.packet.IPacketServer;
import cofh.core.network.packet.PacketBase;
import cofh.core.util.control.IRedstoneControllableTile;
import cofh.lib.api.control.IRedstoneControllable.ControlMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.lib.util.Constants.PACKET_REDSTONE_CONTROL;

public class RedstoneControlPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected int threshold;
    protected byte mode;

    public RedstoneControlPacket() {

        super(PACKET_REDSTONE_CONTROL, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;
        if (!world.isLoaded(pos)) {
            return;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof IRedstoneControllableTile) {
            ((IRedstoneControllableTile) tile).setControl(threshold, ControlMode.VALUES[mode]);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeInt(threshold);
        buf.writeByte(mode);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
        threshold = buf.readInt();
        mode = buf.readByte();
    }

    public static void sendToServer(IRedstoneControllableTile tile) {

        RedstoneControlPacket packet = new RedstoneControlPacket();
        packet.pos = tile.pos();
        packet.threshold = tile.redstoneControl().getThreshold();
        packet.mode = (byte) tile.redstoneControl().getMode().ordinal();
        packet.sendToServer();
    }

}
