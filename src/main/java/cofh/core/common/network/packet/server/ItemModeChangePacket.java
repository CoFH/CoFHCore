package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.ItemModeChangePayload;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ItemModeChangePacket {

    public static final ItemModeChangePacket INSTANCE = new ItemModeChangePacket();

    public static ItemModeChangePacket get() {

        return INSTANCE;
    }

    public void handle(final ItemModeChangePayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();
            boolean decr = payload.decr();

            if (!ItemHelper.isPlayerHoldingMultiModeItem(player)) {
                return;
            }
            if (decr && ItemHelper.decrHeldMultiModeItemState(player) || !decr && ItemHelper.incrHeldMultiModeItemState(player)) {
                ItemHelper.onHeldMultiModeItemChange(player);
            }
        });
    }

    public static void incrMode() {

        sendToServer(false);
    }

    public static void decrMode() {

        sendToServer(true);
    }

    private static void sendToServer(boolean decr) {

        PacketDistributor.SERVER.noArg().send(new ItemModeChangePayload(decr));
    }

}
