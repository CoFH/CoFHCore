package cofh.core.client.gui.element;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public class SimpleTooltip implements ITooltipFactory {

    protected final Component tooltip;

    public SimpleTooltip(Component tooltip) {

        this.tooltip = tooltip;
    }

    @Override
    public List<Component> create(ElementBase element, int mouseX, int mouseY) {

        if (element.visible()) {
            return Collections.singletonList(tooltip);
        }
        return Collections.emptyList();
    }

}
