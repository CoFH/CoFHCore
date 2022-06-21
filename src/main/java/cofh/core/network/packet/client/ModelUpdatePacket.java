package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static cofh.core.network.packet.PacketIDs.PACKET_MODEL_UPDATE;
import static cofh.lib.util.Constants.NETWORK_UPDATE_DISTANCE;

/**
 * A generic packet to force a client-side ModelData update and rerender if it didn't happen for efficiency or other reasons.
 */
public class ModelUpdatePacket extends PacketBase implements IPacketClient {

    protected BlockPos pos;

    public ModelUpdatePacket() {

        super(PACKET_MODEL_UPDATE, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Level level = ProxyUtils.getClientWorld();
        if (level == null) {
            handler.log().error("Client world is null! (Is this being called on the server?)");
            return;
        }
        BlockState state = level.getBlockState(pos);
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile != null) {
            tile.requestModelDataUpdate();
        }
        level.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        pos = buf.readBlockPos();
    }

    public static void sendToClient(Level level, BlockPos pos) {

        if (level == null || Utils.isClientWorld(level)) {
            return;
        }
        ModelUpdatePacket packet = new ModelUpdatePacket();
        packet.pos = pos;
        packet.sendToAllAround(packet.pos, NETWORK_UPDATE_DISTANCE, level.dimension());
    }

}
