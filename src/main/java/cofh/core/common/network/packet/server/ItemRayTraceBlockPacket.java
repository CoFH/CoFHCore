package cofh.core.common.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.common.item.IBlockRayTraceItem;
import cofh.core.util.ProxyUtils;
import cofh.lib.common.network.packet.IPacketServer;
import cofh.lib.common.network.packet.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static cofh.core.common.network.packet.PacketIDs.PACKET_ITEM_RAYTRACE_BLOCK;

public class ItemRayTraceBlockPacket extends PacketBase implements IPacketServer {

    protected InteractionHand hand;
    protected Vec3 origin;
    protected BlockHitResult result;

    public ItemRayTraceBlockPacket() {

        super(PACKET_ITEM_RAYTRACE_BLOCK, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IBlockRayTraceItem item) {
            item.handleBlockRayTrace(player.serverLevel(), player, hand, stack, origin, result);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeEnum(hand);
        buf.writeDouble(origin.x);
        buf.writeFloat((float) origin.y);
        buf.writeDouble(origin.z);
        buf.writeBlockHitResult(result);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        this.hand = buf.readEnum(InteractionHand.class);
        this.origin = new Vec3(buf.readDouble(), buf.readFloat(), buf.readDouble());
        this.result = buf.readBlockHitResult();
    }

    public static void sendToServer(Player player, InteractionHand hand, Vec3 origin, BlockHitResult result) {

        if (player.level.isClientSide) {
            Player client = ProxyUtils.getClientPlayer();
            if (client != null && client.equals(player)) {
                ItemRayTraceBlockPacket packet = new ItemRayTraceBlockPacket();
                packet.hand = hand;
                packet.origin = origin;
                packet.result = result;
                packet.sendToServer();
            }
        }
    }

}
