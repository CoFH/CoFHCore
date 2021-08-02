package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;

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

        return Minecraft.getInstance().fontRenderer.getStringWidth(text);
    }

    @Override
    public void draw(MatrixStack matrixStack, ElementListBox listBox, int x, int y, int backColor, int textColor) {

        listBox.getFontRenderer().drawStringWithShadow(matrixStack, text, x, y, textColor);
    }

}
