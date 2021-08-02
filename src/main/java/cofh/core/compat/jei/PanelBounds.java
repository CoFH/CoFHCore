package cofh.core.compat.jei;

import cofh.core.client.gui.ContainerScreenCoFH;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PanelBounds implements IGuiContainerHandler<ContainerScreenCoFH<?>> {

    @Override
    public List<Rectangle2d> getGuiExtraAreas(ContainerScreenCoFH containerScreen) {

        return containerScreen.getPanelBounds();
    }

}
