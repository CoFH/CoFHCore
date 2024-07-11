package cofh.core.common.network.packet.server;

import cofh.core.common.block.entity.ITileXpHandler;
import cofh.core.common.network.data.server.ClaimXPPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ClaimXPPacket {

    public static final ClaimXPPacket INSTANCE = new ClaimXPPacket();

    public static ClaimXPPacket get() {

        return INSTANCE;
    }

    public void handle(final ClaimXPPayload payload, final PlayPayloadContext context) {

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
            if (tile instanceof ITileXpHandler tileXpHandler) {
                tileXpHandler.claimXP(player);
            }
        });
    }

    public static boolean sendToServer(ITileXpHandler tile) {

        if (tile == null) {
            return false;
        }
        PacketDistributor.SERVER.noArg().send(new ClaimXPPayload(tile.pos()));
        return true;
    }

}
