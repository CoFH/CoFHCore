package cofh.core.common.network.packet.client;

import cofh.core.common.network.data.client.PlayerMotionPayload;
import cofh.core.util.ProxyUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class PlayerMotionPacket {

    public static final PlayerMotionPacket INSTANCE = new PlayerMotionPacket();

    public static PlayerMotionPacket get() {

        return INSTANCE;
    }

    public void handle(final PlayerMotionPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> ProxyUtils.getClientPlayer().setDeltaMovement(ProxyUtils.getClientPlayer().getDeltaMovement().add(payload.motionX(), payload.motionY(), payload.motionZ())));
    }

    public static void sendToClient(double x, double y, double z, Player player) {

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.PLAYER.with(serverPlayer).send(new PlayerMotionPayload(x, y, z));
        }
    }

}
