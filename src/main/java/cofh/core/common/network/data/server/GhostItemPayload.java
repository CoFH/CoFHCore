package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record GhostItemPayload(int slotNumber, ItemStack stack, int count) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "ghost_item_packet");

    public GhostItemPayload(final FriendlyByteBuf buf) {

        this(buf.readInt(), buf.readItem(), buf.readInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeInt(slotNumber);
        buf.writeItem(stack);
        buf.writeInt(count);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
