package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.item.IEntityRayTraceItem;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import static cofh.core.network.packet.PacketIDs.PACKET_ITEM_RAYTRACE_ENTITY;

public class ItemRayTraceEntityPacket extends PacketBase implements IPacketServer {

    protected InteractionHand hand;
    protected Vec3 origin;
    protected int targetId;
    protected Vec3 offset;

    public ItemRayTraceEntityPacket() {

        super(PACKET_ITEM_RAYTRACE_ENTITY, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IEntityRayTraceItem item) {
            Entity target = player.level.getEntity(targetId);
            if (target != null) {
                item.handleEntityRayTrace(player.level, player, hand, stack, origin, target, target.position().add(offset));
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeEnum(hand);
        buf.writeDouble(origin.x);
        buf.writeFloat((float) origin.y);
        buf.writeDouble(origin.z);
        buf.writeInt(targetId);
        buf.writeFloat((float) offset.x);
        buf.writeFloat((float) offset.y);
        buf.writeFloat((float) offset.z);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.hand = buf.readEnum(InteractionHand.class);
        this.origin = new Vec3(buf.readDouble(), buf.readFloat(), buf.readDouble());
        this.targetId = buf.readInt();
        this.offset = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    public static void sendToServer(Player player, InteractionHand hand, Vec3 origin, Entity target, Vec3 hit) {

        if (player.level.isClientSide) {
            Player client = CoFHCore.PROXY.getClientPlayer();
            if (client != null && client.equals(player)) {
                ItemRayTraceEntityPacket packet = new ItemRayTraceEntityPacket();
                packet.hand = hand;
                packet.origin = origin;
                packet.targetId = target.getId();
                packet.offset = hit.subtract(target.position());
                packet.sendToServer();
            }
        }
    }

}
