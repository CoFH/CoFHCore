package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.SecurityControlPayload;
import cofh.core.util.control.ISecurableTile;
import cofh.lib.api.control.ISecurable.AccessMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class SecurityControlPacket {

    public static final SecurityControlPacket INSTANCE = new SecurityControlPacket();

    public static SecurityControlPacket get() {

        return INSTANCE;
    }

    public void handle(final SecurityControlPayload payload, final PlayPayloadContext context) {

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
            if (tile instanceof ISecurableTile securableTile) {
                securableTile.setAccess(AccessMode.VALUES[payload.mode()]);
            }
        });
    }

    public static void sendToServer(ISecurableTile tile) {

        if (tile == null) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new SecurityControlPayload(tile.pos(), (byte) tile.securityControl().getAccess().ordinal()));
    }

}
