package cofh.core.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.util.ProxyUtils;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static cofh.lib.util.constants.Constants.NETWORK_UPDATE_DISTANCE;
import static cofh.lib.util.constants.Constants.PACKET_MODEL_UPDATE;

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

        World world = ProxyUtils.getClientWorld();
        if (world == null) {
            CoFHCore.LOG.error("Client world is null! (Is this being called on the server?)");
            return;
        }
        BlockState state = world.getBlockState(pos);
        TileEntity tile = world.getBlockEntity(pos);
        if (tile != null) {
            tile.requestModelDataUpdate();
        }
        world.sendBlockUpdated(pos, state, state, 3);
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public void read(PacketBuffer buf) {

        pos = buf.readBlockPos();
    }

    public static void sendToClient(World world, BlockPos pos) {

        if (world == null || Utils.isClientWorld(world)) {
            return;
        }
        ModelUpdatePacket packet = new ModelUpdatePacket();
        packet.pos = pos;
        packet.sendToAllAround(packet.pos, NETWORK_UPDATE_DISTANCE, world.dimension());
    }

}
