package cofh.core.item;

import cofh.core.network.packet.server.ItemRayTraceBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Item interface that allows you to send arbitrary block raytraces from client to server.
 * Useful for items where client-side raytraces are desired for improved responsiveness.
 */
public interface IBlockRayTraceItem {

    default void sendBlockRayTrace(Player player, BlockPos pos, Vec3 origin, Vec3 hit) {

        ItemRayTraceBlockPacket.sendToServer(player, pos, origin, hit);
    }

    void handleBlockRayTrace(Level level, ItemStack stack, Player player, BlockPos pos, Vec3 origin, Vec3 hit);
}
