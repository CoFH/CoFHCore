package cofh.core.network.packet.server;

import cofh.core.CoFHCore;
import cofh.core.util.filter.FilterHolderType;
import cofh.core.util.filter.IFilterable;
import cofh.core.util.filter.IFilterableItem;
import cofh.lib.network.packet.IPacketServer;
import cofh.lib.network.packet.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static cofh.core.network.packet.PacketIDs.PACKET_FILTERABLE_GUI_OPEN;
import static cofh.core.util.filter.FilterHolderType.*;

public class FilterableGuiTogglePacket extends PacketBase implements IPacketServer {

    public static byte FILTER_GUI = 0;
    public static byte GUI = 1;

    protected FilterHolderType type;
    protected int entityId = -1;
    protected BlockPos pos = BlockPos.ZERO;
    protected byte mode;

    public FilterableGuiTogglePacket() {

        super(PACKET_FILTERABLE_GUI_OPEN, CoFHCore.PACKET_HANDLER);
    }

    @Override
    public void handleServer(ServerPlayer player) {

        Level world = player.level;

        switch (type) {
            case ITEM -> {
                ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (held.getItem() instanceof IFilterableItem filterable) {
                    if (mode == GUI) {
                        filterable.openGui(player, held);
                    } else if (mode == FILTER_GUI) {
                        filterable.openFilterGui(player, held);
                    }
                }
            }
            case ENTITY -> {
                Entity entity = world.getEntity(entityId);
                if (entity == null || entity.isRemoved()) {
                    return;
                }
                if (entity instanceof IFilterable filterable) {
                    if (mode == GUI) {
                        filterable.openGui(player);
                    } else if (mode == FILTER_GUI) {
                        filterable.openFilterGui(player);
                    }
                }
            }
            case TILE -> {
                if (!world.isLoaded(pos)) {
                    return;
                }
                BlockEntity tile = world.getBlockEntity(pos);
                if (tile instanceof IFilterable filterable) {
                    if (mode == GUI) {
                        filterable.openGui(player);
                    } else if (mode == FILTER_GUI) {
                        filterable.openFilterGui(player);
                    }
                }
            }
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeVarInt(type.ordinal());
        buf.writeVarInt(entityId);
        buf.writeBlockPos(pos);
        buf.writeByte(mode);
    }

    @Override
    public void read(FriendlyByteBuf buf) {

        type = FilterHolderType.from(buf.readVarInt());
        entityId = buf.readVarInt();
        pos = buf.readBlockPos();
        mode = buf.readByte();
    }

    // Unused as ambiguous call not required.
    //    public static void openFilterGui(IFilterable filterable) {
    //
    //        if (filterable instanceof BlockEntity tile) {
    //            openFilterGui(tile);
    //        } else if (filterable instanceof Entity entity) {
    //            openFilterGui(entity);
    //        }
    //    }

    public static void openGui(IFilterable filterable) {

        if (filterable instanceof BlockEntity tile) {
            openGui(tile);
        } else if (filterable instanceof Entity entity) {
            openGui(entity);
        }
    }

    // region ITEMS
    public static void openFilterGui(ItemStack stack) {

        sendToServer(FILTER_GUI);
    }

    public static void openGui(ItemStack stack) {

        sendToServer(GUI);
    }

    protected static void sendToServer(byte mode) {

        FilterableGuiTogglePacket packet = new FilterableGuiTogglePacket();
        packet.type = ITEM;
        packet.mode = mode;
        packet.sendToServer();
    }
    // endregion

    // region TILES
    public static void openFilterGui(BlockEntity tile) {

        sendToServer(tile.getBlockPos(), FILTER_GUI);
    }

    public static void openGui(BlockEntity tile) {

        sendToServer(tile.getBlockPos(), GUI);
    }

    protected static void sendToServer(BlockPos pos, byte mode) {

        FilterableGuiTogglePacket packet = new FilterableGuiTogglePacket();
        packet.type = TILE;
        packet.pos = pos;
        packet.mode = mode;
        packet.sendToServer();
    }
    // endregion

    // region ENTITIES
    public static void openFilterGui(Entity entity) {

        sendToServer(entity.getId(), FILTER_GUI);
    }

    public static void openGui(Entity entity) {

        sendToServer(entity.getId(), GUI);
    }

    protected static void sendToServer(int entityId, byte mode) {

        FilterableGuiTogglePacket packet = new FilterableGuiTogglePacket();
        packet.type = ENTITY;
        packet.entityId = entityId;
        packet.mode = mode;
        packet.sendToServer();
    }
    // endregion
}
