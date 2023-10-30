package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ListBoxElementText implements IListBoxElement {

    private final String text;

    public ListBoxElementText(String text) {

        this.text = text;
    }

    @Override
    public Object getValue() {

        return text;
    }

    @Override
    public int getHeight() {

        return 10;
    }

    @Override
    public int getWidth() {

        return Minecraft.getInstance().font.width(text);
    }

    @Override
    public void draw(GuiGraphics pGuiGraphics, ElementListBox listBox, int x, int y, int backColor, int textColor) {

        pGuiGraphics.drawString(listBox.fontRenderer(), text, x, y, textColor, true);
    }

}
