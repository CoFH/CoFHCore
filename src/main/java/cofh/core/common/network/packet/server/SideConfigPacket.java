package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.SideConfigPayload;
import cofh.core.util.control.IReconfigurableTile;
import cofh.lib.api.control.IReconfigurable.SideConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

import static cofh.lib.api.control.IReconfigurable.SideConfig.SIDE_NONE;

public class SideConfigPacket {

    public static final SideConfigPacket INSTANCE = new SideConfigPacket();

    public static SideConfigPacket get() {

        return INSTANCE;
    }

    public void handle(final SideConfigPayload payload, final PlayPayloadContext context) {

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
            if (tile instanceof IReconfigurableTile reconfigurableTile) {
                byte[] bSides = payload.sides();
                SideConfig[] sides = {SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE, SIDE_NONE};
                if (bSides.length == 6) {
                    for (int i = 0; i < 6; ++i) {
                        if (bSides[i] > SideConfig.VALUES.length) {
                            bSides[i] = 0;
                        }
                        sides[i] = SideConfig.VALUES[bSides[i]];
                    }
                }
                reconfigurableTile.reconfigControl().setSideConfig(sides);
            }
        });
    }

    public static void sendToServer(IReconfigurableTile tile) {

        if (tile == null) {
            return;
        }
        byte[] bSides = new byte[6];
        for (int i = 0; i < 6; ++i) {
            bSides[i] = (byte) tile.reconfigControl().getSideConfig()[i].ordinal();
        }
        PacketDistributor.SERVER.noArg().send(new SideConfigPayload(tile.pos(), bSides));
    }

}
