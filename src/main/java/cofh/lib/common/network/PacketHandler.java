package cofh.lib.common.network;

import cofh.lib.common.network.packet.IPacket;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.IPacketServer;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketHandler {

    private final ResourceLocation channelName;
    private final EventNetworkChannel channel;
    private final Byte2ObjectMap<Supplier<IPacket>> packets = new Byte2ObjectArrayMap<>(255);

    private final Logger log;

    public PacketHandler(ResourceLocation channelName, Logger log) {

        this.channelName = channelName;
        this.log = log;
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

    @SuppressWarnings ("unchecked")
    public <T extends IPacket> void registerPacket(int id, Supplier<? super T> constructor) {

        if (id <= 0 || id >= 255) {
            throw new IllegalArgumentException(String.format("Packet id(%s) not within bounds id <= 0 || id >= 255", id));
        }
        packets.put((byte) id, (Supplier<IPacket>) constructor);
        log.debug("Channel {}, Register packet, ID: {}", channelName, id);
    }

    public ResourceLocation getChannelName() {

        return channelName;
    }

    public Logger log() {

        return log;
    }

    // The ClientHandler, handles packets sent from the server to the client.
    private class ClientHandler {

        @SubscribeEvent
        public void onPayload(NetworkEvent.ServerCustomPayloadEvent event) {

            ByteBuf payload = event.getPayload().copy();
            FriendlyByteBuf buf = new FriendlyByteBuf(payload);

            NetworkEvent.Context ctx = event.getSource().get();
            ctx.setPacketHandled(true);
            byte id = (byte) buf.readUnsignedByte();
            Supplier<IPacket> supplier = packets.get(id);
            if (supplier == null) {
                log.error("Received unregistered packet! ID: {}, Side: Client", id);
                return;
            }
            IPacket packet = supplier.get();
            if (!(packet instanceof IPacketClient)) {
                log.error("Received packet ID that isn't an IPacketClient? ID: {}", id);
                return;
            }

            packet.read(buf);
            ctx.enqueueWork(() -> {
                try {
                    ((IPacketClient) packet).handleClient();
                } catch (Throwable ex) {
                    log.error("Error handling packet on channel {}.", channelName, ex);
                } finally {
                    buf.release();
                }
            });
        }

    }

    // The ServerHandler, handles packets sent from the client to the server.
    private class ServerHandler {

        @SubscribeEvent
        public void onPayload(NetworkEvent.ClientCustomPayloadEvent event) {

            ByteBuf payload = event.getPayload().copy();
            FriendlyByteBuf buf = new FriendlyByteBuf(payload);

            NetworkEvent.Context ctx = event.getSource().get();
            ctx.setPacketHandled(true);
            byte id = (byte) buf.readUnsignedByte();
            Supplier<IPacket> supplier = packets.get(id);
            if (supplier == null) {
                log.error("Received unregistered packet! ID: {}, Side: Server", id);
                return;
            }
            IPacket packet = supplier.get();
            if (!(packet instanceof IPacketServer)) {
                log.error("Received packet ID that isn't an IPacketServer? ID: {}", id);
                return;
            }

            packet.read(buf);
            PacketListener netHandler = ctx.getNetworkManager().getPacketListener();
            if (netHandler instanceof ServerGamePacketListenerImpl gamePacketListener) {
                ctx.enqueueWork(() -> {
                    try {
                        ((IPacketServer) packet).handleServer(gamePacketListener.player);
                    } catch (Throwable ex) {
                        log.error("Error handling packet on channel {}.", channelName, ex);
                    } finally {
                        buf.release();
                    }
                });
            }
        }

    }

}
