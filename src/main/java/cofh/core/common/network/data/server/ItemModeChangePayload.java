package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record ItemModeChangePayload(boolean decr) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "item_mode_change_packet");

    public ItemModeChangePayload(final FriendlyByteBuf buf) {

        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBoolean(decr);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
