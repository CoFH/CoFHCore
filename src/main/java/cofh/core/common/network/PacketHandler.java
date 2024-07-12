package cofh.core.common.network;

import cofh.core.common.network.data.client.*;
import cofh.core.common.network.data.server.*;
import cofh.core.common.network.packet.client.*;
import cofh.core.common.network.packet.server.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class PacketHandler {

    public static void registerNetworking(final RegisterPayloadHandlerEvent event) {

        final IPayloadRegistrar registrar = event.registrar(ID_COFH_CORE);

        // SERVER
        registrar.play(ClaimXPPayload.ID, ClaimXPPayload::new, handler -> handler.server(ClaimXPPacket.get()::handle));
        registrar.play(ContainerConfigPayload.ID, ContainerConfigPayload::new, handler -> handler.server(ContainerConfigPacket.get()::handle));
        registrar.play(FilterableGuiTogglePayload.ID, FilterableGuiTogglePayload::new, handler -> handler.server(FilterableGuiTogglePacket.get()::handle));
        registrar.play(GhostItemPayload.ID, GhostItemPayload::new, handler -> handler.server(GhostItemPacket.get()::handle));
        registrar.play(ItemLeftClickPayload.ID, ItemLeftClickPayload::new, handler -> handler.server(ItemLeftClickPacket.get()::handle));
        registrar.play(ItemModeChangePayload.ID, ItemModeChangePayload::new, handler -> handler.server(ItemModeChangePacket.get()::handle));
        registrar.play(ItemRayTraceBlockPayload.ID, ItemRayTraceBlockPayload::new, handler -> handler.server(ItemRayTraceBlockPacket.get()::handle));
        registrar.play(ItemRayTraceEntityPayload.ID, ItemRayTraceEntityPayload::new, handler -> handler.server(ItemRayTraceEntityPacket.get()::handle));
        registrar.play(RedstoneControlPayload.ID, RedstoneControlPayload::new, handler -> handler.server(RedstoneControlPacket.get()::handle));
        registrar.play(SecurityControlPayload.ID, SecurityControlPayload::new, handler -> handler.server(SecurityControlPacket.get()::handle));
        registrar.play(SecurityPayload.ID, SecurityPayload::new, handler -> handler.server(SecurityPacket.get()::handle));
        registrar.play(SideConfigPayload.ID, SideConfigPayload::new, handler -> handler.server(SideConfigPacket.get()::handle));
        registrar.play(StorageClearPayload.ID, StorageClearPayload::new, handler -> handler.server(StorageClearPacket.get()::handle));
        registrar.play(TileConfigPayload.ID, TileConfigPayload::new, handler -> handler.server(TileConfigPacket.get()::handle));
        registrar.play(TransferControlPayload.ID, TransferControlPayload::new, handler -> handler.server(TransferControlPacket.get()::handle));

        // CLIENT
        registrar.play(ContainerGuiPayload.ID, ContainerGuiPayload::new, handler -> handler.client(ContainerGuiPacket.get()::handle));
        registrar.play(EffectAddedPayload.ID, EffectAddedPayload::new, handler -> handler.client(EffectAddedPacket.get()::handle));
        registrar.play(EffectRemovedPayload.ID, EffectRemovedPayload::new, handler -> handler.client(EffectRemovedPacket.get()::handle));
        registrar.play(ModelUpdatePayload.ID, ModelUpdatePayload::new, handler -> handler.client(ModelUpdatePacket.get()::handle));
        registrar.play(OverlayMessagePayload.ID, OverlayMessagePayload::new, handler -> handler.client(OverlayMessagePacket.get()::handle));
        registrar.play(PlayerMotionPayload.ID, PlayerMotionPayload::new, handler -> handler.client(PlayerMotionPacket.get()::handle));
        registrar.play(TileControlPayload.ID, TileControlPayload::new, handler -> handler.client(TileControlPacket.get()::handle));
        registrar.play(TileGuiPayload.ID, TileGuiPayload::new, handler -> handler.client(TileGuiPacket.get()::handle));
        registrar.play(TileRedstonePayload.ID, TileRedstonePayload::new, handler -> handler.client(TileRedstonePacket.get()::handle));
        registrar.play(TileRenderPayload.ID, TileRenderPayload::new, handler -> handler.client(TileRenderPacket.get()::handle));
        registrar.play(TileStatePayload.ID, TileStatePayload::new, handler -> handler.client(TileStatePacket.get()::handle));
    }

}