package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.helpers.ItemHelper;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static cofh.core.network.packet.PacketIDs.PACKET_ITEM_RAYTRACE_BLOCK;
import static cofh.core.network.packet.PacketIDs.PACKET_ITEM_RAYTRACE_ENTITY;

public class ItemRayTraceBlockPacket extends PacketBase implements IPacketServer {

    protected BlockPos pos;
    protected Vec3 origin;
    protected Vec3 hit;

    public ItemRayTraceBlockPacket() {

        super(PACKET_ITEM_RAYTRACE_BLOCK, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        if (!ItemHelper.isPlayerHoldingEntityRayTraceItem(player)) {
            return;
        }
        ItemHelper.onRayTraceBlock(player, pos, origin, hit);
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeDouble(origin.x);
        buf.writeDouble(origin.y);
        buf.writeDouble(origin.z);
        buf.writeDouble(hit.x);
        buf.writeDouble(hit.y);
        buf.writeDouble(hit.z);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.pos = buf.readBlockPos();
        this.origin = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.hit = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(Player player, BlockPos pos, Vec3 origin, Vec3 hit) {

        if (player.level.isClientSide) {
            Player client = Minecraft.getInstance().player;
            if (client != null && client.equals(player)) {
                ItemRayTraceBlockPacket packet = new ItemRayTraceBlockPacket();
                packet.pos = pos;
                packet.origin = origin;
                packet.hit = hit;
                packet.sendToServer();
            }
        }
    }

}
