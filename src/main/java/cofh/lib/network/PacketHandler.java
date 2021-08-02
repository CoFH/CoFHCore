package cofh.lib.network;

import cofh.lib.network.packet.IPacket;
import cofh.lib.network.packet.IPacketClient;
import cofh.lib.network.packet.IPacketServer;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketHandler {

    private static final Logger LOG = LogManager.getLogger("cofh.PacketHandler");

    private final ResourceLocation channelName;
    private final EventNetworkChannel channel;
    private final Byte2ObjectMap<Supplier<IPacket>> packets = new Byte2ObjectArrayMap<>(255);

    public PacketHandler(ResourceLocation channelName) {

        this.channelName = channelName;
        channel = NetworkRegistry.ChannelBuilder.named(channelName)
                .networkProtocolVersion(() -> Integer.toString(1))
                .clientAcceptedVersions(e -> true)
                .serverAcceptedVersions(e -> true)
                .eventNetworkChannel();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            channel.registerObject(new ClientHandler());
        }
        channel.registerObject(new ServerHandler());
    }

    @SuppressWarnings("unchecked")
    public <T extends IPacket> void registerPacket(int id, Supplier<? super T> constructor) {

        if (id <= 0 || id >= 255) {
            throw new IllegalArgumentException(String.format("Packet id(%s) not within bounds id <= 0 || id >= 255", id));
        }
        packets.put((byte) id, (Supplier<IPacket>) constructor);
        LOG.debug("Channel {}, Register packet, ID: {}", channelName, id);
    }

    public ResourceLocation getChannelName() {

        return channelName;
    }

    // The ClientHandler, handles packets sent from the server to the client.
    private class ClientHandler {

        @SubscribeEvent
        public void onPayload(NetworkEvent.ServerCustomPayloadEvent event) {

            PacketBuffer buf = new PacketBuffer(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            ctx.setPacketHandled(true);
            byte id = (byte) buf.readUnsignedByte();
            Supplier<IPacket> supplier = packets.get(id);
            if (supplier == null) {
                LOG.error("Received unregistered packet! ID: {}, Side: Client", id);
                return;
            }
            IPacket packet = supplier.get();
            if (!(packet instanceof IPacketClient)) {
                LOG.error("Received packet ID that isn't an IPacketClient? ID: {}", id);
                return;
            }
            ctx.enqueueWork(() -> {
                packet.read(buf);
                ((IPacketClient) packet).handleClient();
            });
        }

    }

    // The ServerHandler, handles packets sent from the client to the server.
    private class ServerHandler {

        @SubscribeEvent
        public void onPayload(NetworkEvent.ClientCustomPayloadEvent event) {

            PacketBuffer buf = new PacketBuffer(event.getPayload());
            NetworkEvent.Context ctx = event.getSource().get();
            ctx.setPacketHandled(true);
            byte id = (byte) buf.readUnsignedByte();
            Supplier<IPacket> supplier = packets.get(id);
            if (supplier == null) {
                LOG.error("Received unregistered packet! ID: {}, Side: Server", id);
                return;
            }
            IPacket packet = supplier.get();
            if (!(packet instanceof IPacketServer)) {
                LOG.error("Received packet ID that isn't an IPacketServer? ID: {}", id);
                return;
            }
            INetHandler netHandler = ctx.getNetworkManager().getNetHandler();
            if (netHandler instanceof ServerPlayNetHandler) {
                ctx.enqueueWork(() -> {
                    packet.read(buf);
                    ((IPacketServer) packet).handleServer(((ServerPlayNetHandler) netHandler).player);
                });
            }
        }

    }

}
