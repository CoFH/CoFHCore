package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.SecurityPayload;
import cofh.lib.api.control.ISecurable;
import cofh.lib.api.control.ISecurable.AccessMode;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class SecurityPacket {

    public static final SecurityPacket INSTANCE = new SecurityPacket();

    public static SecurityPacket get() {

        return INSTANCE;
    }

    public void handle(final SecurityPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            if (player.containerMenu instanceof ISecurable securable) {
                securable.setAccess(AccessMode.VALUES[payload.mode()]);
            }
        });
    }

    public static void sendToServer(AccessMode accessMode) {

        PacketDistributor.SERVER.noArg().send(new SecurityPayload((byte) accessMode.ordinal()));
    }

}
