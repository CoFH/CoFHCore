package cofh.core.common.network.packet.client;

import cofh.core.common.network.data.client.TileStatePayload;
import cofh.core.util.ProxyUtils;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import cofh.lib.util.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class TileStatePacket {

    public static final TileStatePacket INSTANCE = new TileStatePacket();

    public static TileStatePacket get() {

        return INSTANCE;
    }

    public void handle(final TileStatePayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Level world = ProxyUtils.getClientWorld();

            BlockPos pos = payload.pos();

            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof IPacketHandlerTile handlerTile) {
                handlerTile.handleStatePacket(payload.buf());
                BlockState state = world.getBlockState(pos);
                world.sendBlockUpdated(pos, state, state, 3);
            }
        });
    }

    public static void sendToClient(IPacketHandlerTile tile) {

        if (tile == null || tile.world() == null || tile.world().isClientSide) {
            return;
        }
        PacketDistributor.NEAR.with(Utils.createTargetPoint(tile.world(), tile.pos())).send(new TileStatePayload(tile.pos(), tile.getStatePacket(new FriendlyByteBuf(Unpooled.buffer()))));
    }

}
