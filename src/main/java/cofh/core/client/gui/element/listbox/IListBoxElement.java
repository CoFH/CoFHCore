package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import com.mojang.blaze3d.vertex.PoseStack;

public interface IListBoxElement {

    int getHeight();

    int getWidth();

    Object getValue();

    void draw(PoseStack matrixStack, ElementListBox listBox, int x, int y, int backColor, int textColor);

}
