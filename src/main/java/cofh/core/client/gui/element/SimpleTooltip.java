package cofh.core.client.gui.element;

import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

public class SimpleTooltip implements ITooltipFactory {

    protected final ITextComponent tooltip;

    public SimpleTooltip(ITextComponent tooltip) {

        this.tooltip = tooltip;
    }

    @Override
    public List<ITextComponent> create(ElementBase element, int mouseX, int mouseY) {

        if (element.visible()) {
            return Collections.singletonList(tooltip);
        }
        return Collections.emptyList();
    }

}
