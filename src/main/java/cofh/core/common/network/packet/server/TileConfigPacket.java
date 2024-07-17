package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.TileConfigPayload;
import cofh.lib.api.block.entity.IPacketHandlerTile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class TileConfigPacket {

    public static final TileConfigPacket INSTANCE = new TileConfigPacket();

    public static TileConfigPacket get() {

        return INSTANCE;
    }

    public void handle(final TileConfigPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            Level world = player.level;
            if (!world.isLoaded(payload.pos())) {
                return;
            }
            BlockEntity tile = world.getBlockEntity(payload.pos());
            if (tile instanceof IPacketHandlerTile handlerTile) {
                handlerTile.handleConfigPacket(payload.buf());
            }
        });
    }

    public static void sendToServer(IPacketHandlerTile tile) {

        if (tile == null) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new TileConfigPayload(tile.pos(), tile.getConfigPacket(new FriendlyByteBuf(Unpooled.buffer()))));
    }

}
