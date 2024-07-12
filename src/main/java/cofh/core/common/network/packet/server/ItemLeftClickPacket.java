package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.ItemLeftClickPayload;
import cofh.core.util.helpers.ItemHelper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ItemLeftClickPacket {

    public static final ItemLeftClickPacket INSTANCE = new ItemLeftClickPacket();

    public static ItemLeftClickPacket get() {

        return INSTANCE;
    }

    // TODO: Make this a multi-click packet of some sort, or cover more potential options
    public void handle(final ItemLeftClickPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            if (!ItemHelper.isPlayerHoldingLeftClickItem(player)) {
                return;
            }
            ItemHelper.onHeldLeftClickItem(player);
        });
    }

    public static void sendToServer() {

        PacketDistributor.SERVER.noArg().send(new ItemLeftClickPayload());
    }

}
