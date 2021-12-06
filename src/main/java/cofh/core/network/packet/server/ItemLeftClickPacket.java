package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import cofh.lib.util.helpers.ItemHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

import static cofh.lib.util.constants.Constants.PACKET_ITEM_LEFT_CLICK;

public class ItemLeftClickPacket extends PacketBase implements IPacketServer {

    public ItemLeftClickPacket() {

        super(PACKET_ITEM_LEFT_CLICK, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayerEntity player) {

        if (!ItemHelper.isPlayerHoldingLeftClickItem(player)) {
            return;
        }
        ItemHelper.onHeldLeftClickItem(player);
    }

    @Override
    public void write(PacketBuffer buf) {

    }

    @Override
    public void read(PacketBuffer buf) {

    }

    public static void createAndSend() {

        ItemLeftClickPacket packet = new ItemLeftClickPacket();
        packet.sendToServer();
    }

}
