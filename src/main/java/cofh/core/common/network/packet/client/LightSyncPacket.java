package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.common.light.LightSavedData;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import static cofh.core.common.network.packet.PacketIDs.PACKET_LIGHT_SYNC;

public class LightSyncPacket extends PacketBase implements IPacketClient {

    protected FriendlyByteBuf buffer;

    public LightSyncPacket() {

        super(PACKET_LIGHT_SYNC, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        LightSavedData.get(level).fromNetwork(level, buffer);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBytes(buffer);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        buffer = buf;
    }

    public static void sendToClient(ServerPlayer player, ServerLevel level) {

        LightSyncPacket packet = new LightSyncPacket();
        packet.buffer = new FriendlyByteBuf(Unpooled.buffer());
        LightSavedData.get(level).toNetwork(packet.buffer);
        packet.sendToPlayer(player);
    }

}
