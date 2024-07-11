package cofh.core.common.network.packet.client;

import cofh.core.common.network.data.client.OverlayMessagePayload;
import cofh.core.util.ProxyUtils;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class OverlayMessagePacket {

    public static final OverlayMessagePacket INSTANCE = new OverlayMessagePacket();

    public static OverlayMessagePacket get() {

        return INSTANCE;
    }

    public void handle(final OverlayMessagePayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> ProxyUtils.setOverlayMessage(StringHelper.fromJSON(payload.message())));
    }

    public static void sendToClient(Component message, ServerPlayer player) {

        PacketDistributor.PLAYER.with(player).send(new OverlayMessagePayload(StringHelper.toJSON(message)));
    }

}
