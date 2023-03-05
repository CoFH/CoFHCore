package cofh.core.item;

import cofh.core.network.packet.server.ItemRayTraceEntityPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Item interface that allows you to send arbitrary entity raytraces from client to server.
 * Useful for items where client-side raytraces are desired for improved responsiveness.
 */
public interface IEntityRayTraceItem {

    default void sendEntityRayTrace(Player player, InteractionHand hand, Vec3 origin, Entity target, Vec3 hit) {

        ItemRayTraceEntityPacket.sendToServer(player, hand, origin, target, hit);
    }

    void handleEntityRayTrace(Level level, Player player, InteractionHand hand, ItemStack stack, Vec3 origin, Entity target, Vec3 hit);

}
