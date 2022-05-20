package cofh.lib.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityEnchantableItem {

    public static final Capability<IEnchantableItem> ENCHANTABLE_ITEM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {

        event.register(IEnchantableItem.class);
    }

}
