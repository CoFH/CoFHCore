package cofh.core.client.gui.element;

import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

public interface ITooltipFactory {

    List<ITextComponent> create(ElementBase element, int mouseX, int mouseY);

    ITooltipFactory EMPTY = (element, mouseX, mouseY) -> Collections.emptyList();

}