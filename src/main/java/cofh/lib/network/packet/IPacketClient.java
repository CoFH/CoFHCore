package cofh.lib.network.packet;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkDirection;

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

        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        PlayerList list = server.getPlayerList();
        list.sendPacketToAllPlayers(toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
    }

    /**
     * Sends this packet to the specified player.
     *
     * @param player The player to send the packet to.
     */
    default void sendToPlayer(ServerPlayerEntity player) {

        player.connection.sendPacket(toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
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

        net.minecraft.network.IPacket<?> packet = null;
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        PlayerList playerList = server.getPlayerList();
        for (ServerPlayerEntity player : playerList.getPlayers()) {
            if (playerList.canSendCommands(player.getGameProfile())) {
                if (packet == null) { // So we don't serialize multiple times.
                    packet = toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT);
                }
                player.connection.sendPacket(packet);
            }
        }
    }

    // region sendToAllAround

    /**
     * Sends this packet to all players around the specified point.
     * This method is an overload of {@link #sendToAllAround(double, double, double, double, RegistryKey<World>)}
     *
     * @param pos   The pos.
     * @param range The range.
     * @param dim   The dimension
     */
    default void sendToAllAround(BlockPos pos, double range, RegistryKey<World> dim) {

        sendToAllAround(pos.getX(), pos.getY(), pos.getZ(), range, dim);
    }

    /**
     * Sends this packet to all players around the specified point.
     * This method is an overload of {@link #sendToAllAround(double, double, double, double, RegistryKey<World>)}
     *
     * @param pos   The pos.
     * @param range The range.
     * @param dim   The dimension
     */
    default void sendToAllAround(Vector3d pos, double range, RegistryKey<World> dim) {

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
    default void sendToAllAround(double x, double y, double z, double range, RegistryKey<World> dim) {

        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        PlayerList list = server.getPlayerList();
        list.sendToAllNearExcept(null, x, y, z, range, dim, toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT));
    }
    // endregion

    // region sendToChunk

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerWorld, BlockPos)}
     *
     * @param tile The tile, used as a reference for the chunk.
     */
    default void sendToChunk(TileEntity tile) {

        sendToChunk((ServerWorld) tile.getWorld(), tile.getPos());
    }

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerWorld, int, int)}
     *
     * @param world The world the chunk is in.
     * @param pos   The pos, This is a position of a block, not a chunk.
     */
    default void sendToChunk(ServerWorld world, BlockPos pos) {

        sendToChunk(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Sends this packet to all clients watching the chunk.
     * This method is an overload of {@link #sendToChunk(ServerWorld, ChunkPos)}
     *
     * @param world  The world.
     * @param chunkX The chunk's X coord.
     * @param chunkZ The chunk'z Z coord.
     */
    default void sendToChunk(ServerWorld world, int chunkX, int chunkZ) {

        sendToChunk(world, new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Sends this packet to all clients watching the chunk.
     *
     * @param world The world.
     * @param pos   The pos.
     */
    default void sendToChunk(ServerWorld world, ChunkPos pos) {

        net.minecraft.network.IPacket<?> packet = toVanillaPacket(NetworkDirection.PLAY_TO_CLIENT);
        world.getChunkProvider().chunkManager.getTrackingPlayers(pos, false)//
                .forEach(e -> e.connection.sendPacket(packet));
    }
    // endregion
}
