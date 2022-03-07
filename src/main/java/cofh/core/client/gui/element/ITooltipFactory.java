package cofh.core.client.gui.element;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public interface ITooltipFactory {

    List<Component> create(ElementBase element, int mouseX, int mouseY);

    ITooltipFactory EMPTY = (element, mouseX, mouseY) -> Collections.emptyList();

}
