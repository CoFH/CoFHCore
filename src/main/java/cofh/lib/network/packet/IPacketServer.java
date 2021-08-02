package cofh.lib.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

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
    void handleServer(ServerPlayerEntity player);

    /**
     * Send this packet to the server.
     */
    @OnlyIn(Dist.CLIENT)
    default void sendToServer() {

        Minecraft.getInstance().getConnection().sendPacket(toVanillaPacket(NetworkDirection.PLAY_TO_SERVER));
    }

}
