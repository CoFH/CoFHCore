package cofh.core.compat.jei;

import cofh.core.client.gui.ContainerScreenCoFH;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@JeiPlugin
public class CoreJeiPlugin implements IModPlugin {

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

        registration.addGenericGuiContainerHandler(ContainerScreenCoFH.class, new PanelBounds());
    }

    @Override
    public ResourceLocation getPluginUid() {

        return new ResourceLocation(ID_COFH_CORE, "default");
    }

}
