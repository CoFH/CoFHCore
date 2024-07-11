package cofh.core.common.network.packet.server;

import cofh.core.common.inventory.ContainerMenuCoFH;
import cofh.core.common.network.data.server.GhostItemPayload;
import cofh.lib.common.inventory.SlotFalseCopy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

import static cofh.core.util.helpers.ItemHelper.cloneStack;

public class GhostItemPacket {

    public static final GhostItemPacket INSTANCE = new GhostItemPacket();

    public static GhostItemPacket get() {

        return INSTANCE;
    }

    public void handle(final GhostItemPayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            Player player = senderOptional.get();

            if (player.containerMenu instanceof ContainerMenuCoFH container) {
                Slot slot = container.getSlot(payload.slotNumber());
                if (slot instanceof SlotFalseCopy) {
                    slot.set(cloneStack(payload.stack(), payload.count()));
                }
            }
        });
    }

    public static void sendToServer(int slotNumber, ItemStack stack, int count) {

        if (slotNumber < 0 || stack.isEmpty() || count < 0) {
            return;
        }
        PacketDistributor.SERVER.noArg().send(new GhostItemPayload(slotNumber, stack, count));
    }

}
