package cofh.core.common.network.data.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record TransferControlPayload(BlockPos pos, boolean transferIn,
                                     boolean transferOut) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "transfer_control_packet");

    public TransferControlPayload(final FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeBlockPos(pos);
        buf.writeBoolean(transferIn);
        buf.writeBoolean(transferOut);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
