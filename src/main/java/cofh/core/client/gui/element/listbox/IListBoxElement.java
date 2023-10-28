package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import net.minecraft.client.gui.GuiGraphics;

public interface IListBoxElement {

    int getHeight();

    int getWidth();

    Object getValue();

    void draw(GuiGraphics pGuiGraphics, ElementListBox listBox, int x, int y, int backColor, int textColor);

}
