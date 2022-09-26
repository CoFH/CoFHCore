package cofh.core.compat.jei;

import cofh.core.client.gui.ContainerScreenCoFH;
import cofh.core.util.references.CoreReferences;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@JeiPlugin
public class CoreJeiPlugin implements IModPlugin {

    @Override
    public void registerFluidSubtypes(ISubtypeRegistration registration) {

        registration.registerSubtypeInterpreter(ForgeTypes.FLUID_STACK, CoreReferences.FLUID_POTION, FluidPotionSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

        registration.addGenericGuiContainerHandler(ContainerScreenCoFH.class, new PanelBounds());
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {

        // registration.addRecipeManagerPlugin(new PotionNBTRecipeManagerPlugin());
    }

    @Override
    public ResourceLocation getPluginUid() {

        return new ResourceLocation(ID_COFH_CORE, "default");
    }

}

