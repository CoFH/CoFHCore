package cofh.core.common.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

public class PacketHandler {

    public static void registerNetworking(final RegisterPayloadHandlerEvent event) {

        final IPayloadRegistrar registrar = event.registrar(ID_COFH_CORE);

        // SERVER

        // CLIENT

    }

}