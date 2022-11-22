package cofh.core.client.gui.element.listbox;

import cofh.core.client.gui.element.ElementListBox;
import com.mojang.blaze3d.vertex.PoseStack;
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

        return Minecraft.getInstance().font.width(text);
    }

    @Override
    public void draw(PoseStack matrixStack, ElementListBox listBox, int x, int y, int backColor, int textColor) {

        listBox.fontRenderer().drawShadow(matrixStack, text, x, y, textColor);
    }

}
