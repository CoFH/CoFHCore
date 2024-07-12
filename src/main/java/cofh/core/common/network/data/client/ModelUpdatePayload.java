package cofh.core.common.network.data.client;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record ModelUpdatePayload(BlockPos pos) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "model_update_packet");

    public ModelUpdatePayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
