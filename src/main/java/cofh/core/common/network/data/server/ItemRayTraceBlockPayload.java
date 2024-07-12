package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record ItemRayTraceBlockPayload(InteractionHand hand, Vec3 origin,
                                       BlockHitResult result) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "item_ray_trace_block_packet");

    public ItemRayTraceBlockPayload(final FriendlyByteBuf buf) {

        this(buf.readEnum(InteractionHand.class), buf.readVec3(), buf.readBlockHitResult());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeEnum(hand);
        buf.writeVec3(origin);
        buf.writeBlockHitResult(result);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
