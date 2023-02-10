package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.inventory.container.ContainerCoFH;
import cofh.lib.inventory.container.slot.SlotFalseCopy;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static cofh.core.network.packet.PacketIDs.PACKET_GHOST_ITEM;
import static cofh.core.util.helpers.ItemHelper.cloneStack;

public class GhostItemPacket extends PacketBase implements IPacketServer {

    protected int slotNumber;
    protected ItemStack stack;
    protected int count;

    public GhostItemPacket() {

        super(PACKET_GHOST_ITEM, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (player.containerMenu instanceof ContainerCoFH container) {
            Slot slot = container.getSlot(slotNumber);
            if (slot instanceof SlotFalseCopy) {
                slot.set(cloneStack(stack, count));
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(slotNumber);
        buf.writeItemStack(stack, false);
        buf.writeInt(count);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        slotNumber = buf.readInt();
        stack = buf.readItem();
        count = buf.readInt();
    }

    public static void sendToServer(int slotNumber, ItemStack stack) {

        sendToServer(slotNumber, stack, 1);
    }

    public static void sendToServer(int slotNumber, ItemStack stack, int count) {

        if (slotNumber < 0 || stack.isEmpty() || count < 0) {
            return;
        }
        GhostItemPacket packet = new GhostItemPacket();
        packet.slotNumber = slotNumber;
        packet.stack = stack;
        packet.count = count;
        packet.sendToServer();
    }

}
