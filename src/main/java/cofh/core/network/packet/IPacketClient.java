package cofh.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Packet sent FROM Servers TO Clients
 *
 * @author covers1624
 */
public interface IPacketClient extends IPacket {

    /**
     * Handle the packet on the client side.
     */
    void handleClient();

    /**
     * Sends this packet to all clients on the server.
     */
    default void sendToClients() {

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        PlayerList list = server.getPlayerList();
        list.broadcastAll(toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    /**
     * Sends this packet to the specified player.
     *
     * @param player The player to send the packet to.
     */
    default void sendToPlayer(ServerPlayer player) {

        player.connection.send(toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    // TODO: Consider fixing if functionality required.
    //    /**
    //     * Sends this packet to all players in the specified dimension.
    //     *
    //     * @param dim The dimension.
    //     */
    //    default void sendToDimension(DimensionType dim) {
    //
    //        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    //        PlayerList list = server.getPlayerList();
    //        list.sendPacketToAllPlayersInDimension(toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT), dim);
    //    }

    /**
     * Sends this packet to all server operators.
     */
    default void sendToOps() {

        Packet<?> packet = null;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayer player : playerList.getPlayers()) {
            if (playerList.isOp(player.getGameProfile())) {
                if (packet == null) { // So we don't serialize multiple times.
                    packet = toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT);
                }
                player.connection.send(packet);
            }
        }
    }

    // region sendToAllAround

    /**
     * Sends this packet to all players around the specified point.
     * This method is an overload of {@link #sendToAllAround(double, double, double, double, ResourceKey<Level>)}
     *
     * @param pos   The pos.
     * @param range The range.
     * @param dim   The dimension
     */
    default void sendToAllAround(BlockPos pos, double range, ResourceKey<Level> dim) {

        sendToAllAround(pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    /**
     * Sends this packet to all players around the specified point.
     * This method is an overload of {@link #sendToAllAround(double, double, double, double, ResourceKey<Level>)}
     *
     * @param pos   The pos.
     * @param range The range.
     * @param dim   The dimension
     */
    default void sendToAllAround(Vec3 pos, double range, ResourceKey<Level> dim) {

        sendToAllAround(pos.x, pos.y, pos.z, range, dim);
    }

    /**
     * Sends this packet to all players around the specified point.
     *
     * @param x     The x position.
     * @param y     The y position.
     * @param z     The z position.
     * @param range The range.
     * @param dim   The dimension
     */
    default void sendToAllAround(double x, double y, double z, double range, ResourceKey<Level> dim) {

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        PlayerList list = server.getPlayerList();
        list.broadcast(null, x, y, z, range, dim, toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
    }
    // endregion

    // region sendToChunk

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerLevel, BlockPos)}
     *
     * @param tile The tile, used as a reference for the chunk.
     */
    default void sendToChunk(BlockEntity tile) {

        sendToChunk((ServerLevel) tile.getLevel(), tile.getBlockPos());
    }

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerLevel, int, int)}
     *
     * @param world The world the chunk is in.
     * @param pos   The pos, This is a position of a block, not a chunk.
     */
    default void sendToChunk(ServerLevel world, BlockPos pos) {

        sendToChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerLevel, ChunkPos)}
     *
     * @param world  The world.
     * @param chunkX The chunk's X coord.
     * @param chunkZ The chunk'z Z coord.
     */
    default void sendToChunk(ServerLevel world, int chunkX, int chunkZ) {

        sendToChunk(world, new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Sends this packet to all clients watching the chunk.
     *
     * @param world The world.
     * @param pos   The pos.
     */
    default void sendToChunk(ServerLevel world, ChunkPos pos) {

        Packet<?> packet = toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT);
        world.getChunkSource().chunkMap.getPlayers(pos, false)
                .forEach(e -> e.connection.send(packet));
    }
    // endregion
}
