package cofh.core.common.network.data.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public record ItemRayTraceEntityPayload(InteractionHand hand, Vec3 origin, int targetId, Vec3 offset,
                                        float power) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(ID_COFH_CORE, "item_ray_trace_entity_packet");

    public ItemRayTraceEntityPayload(final FriendlyByteBuf buf) {

        this(buf.readEnum(InteractionHand.class), buf.readVec3(), buf.readVarInt(), buf.readVec3(), buf.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        buf.writeEnum(hand);
        buf.writeVec3(origin);
        buf.writeVarInt(targetId);
        buf.writeVec3(offset);
        buf.writeFloat(power);
    }

    @Override
    public ResourceLocation id() {

        return ID;
    }

}
