package cofh.core.common.network.packet.client;

import cofh.core.common.network.data.client.TileRedstonePayload;
import cofh.core.util.ProxyUtils;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import cofh.lib.util.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class TileRedstonePacket {

    public static final TileRedstonePacket INSTANCE = new TileRedstonePacket();

    public static TileRedstonePacket get() {

        return INSTANCE;
    }

    public void handle(final TileRedstonePayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Level world = ProxyUtils.getClientWorld();

            BlockPos pos = payload.pos();

            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof IPacketHandlerTile handlerTile) {
                handlerTile.handleRedstonePacket(payload.buf());
            }
        });
    }

    public static void sendToClient(IPacketHandlerTile tile) {

        if (tile == null || tile.world() == null || tile.world().isClientSide) {
            return;
        }
        PacketDistributor.NEAR.with(Utils.createTargetPoint(tile.world(), tile.pos())).send(new TileRedstonePayload(tile.pos(), tile.getRedstonePacket(new FriendlyByteBuf(Unpooled.buffer()))));
    }

}
