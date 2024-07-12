package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class ItemLeftClickPayload implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "item_left_click_packet");

    public ItemLeftClickPayload() {

    }

    public ItemLeftClickPayload(final FriendlyByteBuf buf) {

    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
