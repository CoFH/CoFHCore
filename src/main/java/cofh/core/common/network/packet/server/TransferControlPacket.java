package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.TransferControlPayload;
import cofh.core.util.control.ITransferControllableTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class TransferControlPacket {

    public static final TransferControlPacket INSTANCE = new TransferControlPacket();

    public static TransferControlPacket get() {

        return INSTANCE;
    }

    public void handle(final TransferControlPayload payload, final PlayPayloadContext context) {

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
            if (tile instanceof ITransferControllableTile transferControllableTile) {
                transferControllableTile.setControl(payload.transferIn(), payload.transferOut());
            }
        });
    }

    public static void sendToServer(ITransferControllableTile tile) {

        if (tile == null) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new TransferControlPayload(tile.pos(), tile.transferControl().getTransferIn(), tile.transferControl().getTransferOut()));
    }

}
