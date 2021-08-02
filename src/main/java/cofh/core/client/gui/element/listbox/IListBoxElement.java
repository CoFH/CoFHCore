package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import com.mojang.blaze3d.matrix.MatrixStack;

public interface IListBoxElement {

    int getHeight();

    int getWidth();

    Object getValue();

    void draw(MatrixStack matrixStack, ElementListBox listBox, int x, int y, int backColor, int textColor);

}
