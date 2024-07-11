package cofh.core.common.network.packet.server;

import cofh.core.common.inventory.ContainerMenuCoFH;
import cofh.core.common.network.data.server.ContainerConfigPayload;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ContainerConfigPacket {

    public static final ContainerConfigPacket INSTANCE = new ContainerConfigPacket();

    public static ContainerConfigPacket get() {

        return INSTANCE;
    }

    public void handle(final ContainerConfigPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            if (player.containerMenu instanceof ContainerMenuCoFH container) {
                container.handleConfigPacket(payload.buffer());
            }
        });
    }

    public static void sendToServer(ContainerMenuCoFH container) {

        if (container == null) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new ContainerConfigPayload(container.getConfigPacket(new FriendlyByteBuf(Unpooled.buffer()))));
    }

}
