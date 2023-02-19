package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.helpers.ItemHelper;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import static cofh.core.network.packet.PacketIDs.PACKET_ITEM_RAYTRACE_ENTITY;

public class ItemRayTraceEntityPacket extends PacketBase implements IPacketServer {

    protected int targetId;
    protected Vec3 origin;
    protected Vec3 hit;

    public ItemRayTraceEntityPacket() {

        super(PACKET_ITEM_RAYTRACE_ENTITY, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (!ItemHelper.isPlayerHoldingEntityRayTraceItem(player)) {
            return;
        }
        ItemHelper.onRayTraceEntity(player, targetId, origin, hit);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(targetId);
        buf.writeDouble(origin.x);
        buf.writeFloat((float) origin.y);
        buf.writeDouble(origin.z);
        buf.writeDouble(hit.x);
        buf.writeFloat((float) hit.y);
        buf.writeDouble(hit.z);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.targetId = buf.readInt();
        this.origin = new Vec3(buf.readDouble(), buf.readFloat(), buf.readDouble());
        this.hit = new Vec3(buf.readDouble(), buf.readFloat(), buf.readDouble());
    }

    public static void sendToServer(Player player, Entity target, Vec3 origin, Vec3 hit) {

        if (player.level.isClientSide) {
            Player client = Minecraft.getInstance().player;
            if (client != null && client.equals(player)) {
                ItemRayTraceEntityPacket packet = new ItemRayTraceEntityPacket();
                packet.targetId = target.getId();
                packet.origin = origin;
                packet.hit = hit;
                packet.sendToServer();
            }
        }
    }

}
