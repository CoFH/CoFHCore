package cofh.core.common.network.packet.client;

import cofh.core.CoFHCore;
import cofh.core.common.capability.CapabilityPersistentLight;
import cofh.lib.common.network.packet.IPacketClient;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import static cofh.core.common.network.packet.PacketIDs.PACKET_LIGHT_SYNC;

public class LightSyncPacket extends PacketBase implements IPacketClient {

    protected long chunk;
    protected long[] posns;
    protected double[] radii;

    public LightSyncPacket() {

        super(PACKET_LIGHT_SYNC, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleClient() {

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            level.getChunk(ChunkPos.getX(chunk), ChunkPos.getZ(chunk)).getCapability(CapabilityPersistentLight.LIGHT_CAPABILITY).ifPresent(lights -> {
                for (int i = 0; i < posns.length; ++i) {
                    lights.add(BlockPos.of(posns[i]), radii[i]);
                }
            });
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeLong(chunk);
        buf.writeLongArray(posns);
        for (int i = 0; i < posns.length; ++i) {
            buf.writeDouble(radii[i]);
        }
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.chunk = buf.readLong();
        this.posns = buf.readLongArray();
        this.radii = new double[posns.length];
        for (int i = 0; i < posns.length; ++i) {
            this.radii[i] = buf.readDouble();
        }
    }

    public static void sendToClient(ServerPlayer player, ChunkPos chunk, long[] posns, double[] radii) {

        LightSyncPacket packet = new LightSyncPacket();
        packet.chunk = chunk.toLong();
        packet.posns = posns;
        packet.radii = radii;
        packet.sendToPlayer(player);
    }

}
