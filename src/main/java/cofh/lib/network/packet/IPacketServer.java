package cofh.lib.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

/**
 * Packet sent FROM Clients TO Servers
 *
 * @author covers1624
 */
public interface IPacketServer extends IPacket {

    /**
     * Handle the packet on the server.
     *
     * @param player The player who sent the packet.
     */
    void handleServer(ServerPlayer player);

    /**
     * Send this packet to the server.
     */

    default void sendToServer() {

        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(toVanillaPacket(NetworkDirection.PLAY_TO_SERVER));
        }
    }

}
