package cofh.core.common.network.packet.client;

import cofh.core.common.inventory.ContainerMenuCoFH;
import cofh.core.common.network.data.client.ContainerGuiPayload;
import cofh.core.util.ProxyUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ContainerGuiPacket {

    public static final ContainerGuiPacket INSTANCE = new ContainerGuiPacket();

    public static ContainerGuiPacket get() {

        return INSTANCE;
    }

    public void handle(final ContainerGuiPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Player player = ProxyUtils.getClientPlayer();
            if (player.containerMenu instanceof ContainerMenuCoFH container) {
                container.handleGuiPacket(payload.buf());
            }
        });
    }

    public static void sendToClient(ContainerMenuCoFH container, Player player) {

        if (container == null) {
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.PLAYER.with(serverPlayer).send(new ContainerGuiPayload(container.getGuiPacket(new FriendlyByteBuf(Unpooled.buffer()))));
        }
    }

}
