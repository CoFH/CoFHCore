package cofh.core.common.network.packet.server;

import cofh.core.common.network.data.server.FilterableGuiTogglePayload;
import cofh.core.util.filter.FilterHolderType;
import cofh.core.util.filter.IFilterable;
import cofh.core.util.filter.IFilterableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

import static cofh.core.util.filter.FilterHolderType.*;

public class FilterableGuiTogglePacket {

    public static final FilterableGuiTogglePacket INSTANCE = new FilterableGuiTogglePacket();

    public static FilterableGuiTogglePacket get() {

        return INSTANCE;
    }

    public static byte FILTER_GUI = 0;
    public static byte GUI = 1;

    public void handle(final FilterableGuiTogglePayload payload, final PlayPayloadContext context) {

        context.workHandler().submitAsync(() -> {
            Optional<Player> senderOptional = context.player();
            if (senderOptional.isEmpty()) {
                return;
            }
            ServerPlayer player = (ServerPlayer) senderOptional.get();

            Level world = player.level;

            FilterHolderType type = FilterHolderType.from(payload.type());
            int entityId = payload.entityId();
            BlockPos pos = payload.pos();
            int mode = payload.mode();

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
        });
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

        PacketDistributor.SERVER.noArg().send(new FilterableGuiTogglePayload(ITEM.ordinal(), -1, BlockPos.ZERO, mode));
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

        PacketDistributor.SERVER.noArg().send(new FilterableGuiTogglePayload(TILE.ordinal(), -1, pos, mode));
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

        PacketDistributor.SERVER.noArg().send(new FilterableGuiTogglePayload(ENTITY.ordinal(), entityId, BlockPos.ZERO, mode));
    }
    // endregion
}
