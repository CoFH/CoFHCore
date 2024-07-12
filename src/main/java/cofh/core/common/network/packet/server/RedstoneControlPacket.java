package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.RedstoneControlPayload;
import cofh.core.util.control.IRedstoneControllableTile;
import cofh.lib.api.control.IRedstoneControllable.ControlMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class RedstoneControlPacket {

    public static final RedstoneControlPacket INSTANCE = new RedstoneControlPacket();

    public static RedstoneControlPacket get() {

        return INSTANCE;
    }

    public void handle(final RedstoneControlPayload payload, final PlayPayloadContext context) {

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
            if (tile instanceof IRedstoneControllableTile redstoneControllableTile) {
                redstoneControllableTile.setControl(payload.threshold(), ControlMode.VALUES[payload.mode()]);
            }
        });
    }

    public static void sendToServer(IRedstoneControllableTile tile) {

        if (tile == null) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new RedstoneControlPayload(tile.pos(), tile.redstoneControl().getThreshold(), (byte) tile.redstoneControl().getMode().ordinal()));
    }

}
