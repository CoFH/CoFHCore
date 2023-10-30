package cofh.core.client.gui.element;

import cofh.core.client.gui.GuiColor;
import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.listbox.IListBoxElement;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ElementListBox extends ElementBase {

    public int borderColor = new GuiColor(120, 120, 120, 255).getColor();
    public int backgroundColor = new GuiColor(0, 0, 0, 255).getColor();
    public int selectedLineColor = new GuiColor(0, 0, 0, 255).getColor();
    public int textColor = new GuiColor(150, 150, 150, 255).getColor();
    public int selectedTextColor = new GuiColor(255, 255, 255, 255).getColor();

    protected int marginTop = 2;
    protected int marginLeft = 2;
    protected int marginRight = 2;
    protected int marginBottom = 2;

    protected final List<IListBoxElement> elements = new LinkedList<>();

    protected int firstIndexDisplayed;
    protected int selectedIndex;
    protected int scrollHoriz;

    public ElementListBox(IGuiAccess containerScreen, int x, int y, int width, int height) {

        super(containerScreen, x, y, width, height);
    }

    public void add(IListBoxElement element) {

        elements.add(element);
    }

    public void add(Collection<? extends IListBoxElement> elements) {

        this.elements.addAll(elements);
    }

    public void remove(IListBoxElement element) {

        int e = elements.indexOf(element);
        if (elements.remove(element)) {
            if (e < firstIndexDisplayed) {
                --firstIndexDisplayed;
            }
            if (e < selectedIndex) {
                --selectedIndex;
            }
        }
    }

    public void removeAt(int index) {

        firstIndexDisplayed = scrollHoriz = 0;
        selectedIndex = -1;
        elements.remove(index);
    }

    public void removeAll() {

        elements.clear();
    }

    public int getInternalWidth() {

        int width = 0;
        for (int i = 0; i < elements.size(); ++i) {
            width = Math.max(elements.get(i).getWidth(), width);
        }
        return width;
    }

    public int getInternalHeight() {

        int height = 0;
        for (int i = 0; i < elements.size(); ++i) {
            height += elements.get(i).getHeight();
        }
        return height;
    }

    public int getContentWidth() {

        return width - marginLeft - marginRight;
    }

    public int getContentHeight() {

        return height - marginTop - marginBottom;
    }

    public int getContentTop() {

        return posY() + marginTop;
    }

    public int getContentLeft() {

        return posX() + marginLeft;
    }

    public final int getContentBottom() {

        return getContentTop() + getContentHeight();
    }

    public final int getContentRight() {

        return getContentLeft() + getContentWidth();
    }

    public ElementListBox setTextColor(Number textColor, Number selectedTextColor) {

        if (textColor != null) {
            this.textColor = textColor.intValue();
        }
        if (selectedTextColor != null) {
            this.selectedTextColor = selectedTextColor.intValue();
        }
        return this;
    }

    public ElementListBox setSelectionColor(Number selectedLineColor) {

        if (selectedLineColor != null) {
            this.selectedLineColor = selectedLineColor.intValue();
        }
        return this;
    }

    public ElementListBox setBackgroundColor(Number backgroundColor, Number borderColor) {

        if (backgroundColor != null) {
            this.backgroundColor = backgroundColor.intValue();
        }
        if (borderColor != null) {
            this.borderColor = borderColor.intValue();
        }
        return this;
    }

    @Override
    public void drawBackground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        drawColoredModalRect(pGuiGraphics.pose(), posX() - 1, posY() - 1, posX() + width + 1, posY() + height + 1, borderColor);
        drawColoredModalRect(pGuiGraphics.pose(), posX(), posY(), posX() + width, posY() + height, backgroundColor);
    }

    @Override
    public void drawForeground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        PoseStack poseStack = pGuiGraphics.pose();

        int heightDrawn = 0;
        int nextElement = firstIndexDisplayed;

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        RenderHelper.drawStencil(poseStack, getContentLeft(), getContentTop(), getContentRight(), getContentBottom(), 1);

        poseStack.pushPose();
        poseStack.translate(-scrollHoriz, 0, 0);

        int e = elements.size();
        while (nextElement < e && heightDrawn <= getContentHeight()) {
            heightDrawn += drawElement(pGuiGraphics, nextElement, getContentLeft(), getContentTop() + heightDrawn);
            ++nextElement;
        }
        poseStack.popPose();
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    protected int drawElement(GuiGraphics pGuiGraphics, int elementIndex, int x, int y) {

        IListBoxElement element = elements.get(elementIndex);
        if (elementIndex == selectedIndex) {
            element.draw(pGuiGraphics, this, x, y, selectedLineColor, selectedTextColor);
        } else {
            element.draw(pGuiGraphics, this, x, y, backgroundColor, textColor);
        }

        return element.getHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        int heightChecked = 0;
        for (int i = firstIndexDisplayed; i < elements.size(); ++i) {
            if (heightChecked > getContentHeight()) {
                break;
            }
            int elementHeight = elements.get(i).getHeight();
            if (getContentTop() + heightChecked <= mouseY && getContentTop() + heightChecked + elementHeight >= mouseY) {
                setSelectedIndex(i);
                onElementClicked(elements.get(i));
                break;
            }
            heightChecked += elementHeight;
        }
        return true;
    }

    @Override
    public boolean mouseWheel(double mouseX, double mouseY, double movement) {

        if (Screen.hasControlDown()) {
            if (movement > 0) {
                scrollLeft();
            } else if (movement < 0) {
                scrollRight();
            }
        } else {
            if (movement > 0) {
                scrollUp();
            } else if (movement < 0) {
                scrollDown();
            }
        }
        return true;
    }

    public void scrollDown() {

        int heightDisplayed = 0;
        int elementsDisplayed = 0;
        for (int i = firstIndexDisplayed; i < elements.size(); ++i) {
            if (heightDisplayed + elements.get(i).getHeight() > height) {
                break;
            }
            heightDisplayed += elements.get(i).getHeight();
            ++elementsDisplayed;
        }
        if (firstIndexDisplayed + elementsDisplayed < elements.size()) {
            ++firstIndexDisplayed;
        }
        onScrollV(firstIndexDisplayed);
    }

    public void scrollUp() {

        if (firstIndexDisplayed > 0) {
            --firstIndexDisplayed;
        }
        onScrollV(firstIndexDisplayed);
    }

    public void scrollLeft() {

        scrollHoriz = Math.max(scrollHoriz - 15, 0);
        onScrollH(scrollHoriz);
    }

    public void scrollRight() {

        scrollHoriz = Math.min(scrollHoriz + 15, getLastScrollPositionH());
        onScrollH(scrollHoriz);
    }

    public int getLastScrollPosition() {

        int position = elements.size() - 1;
        if (position < 0) {
            return 0;
        }
        int heightUsed = 0;

        while (position >= 0 && heightUsed < height) {
            heightUsed += elements.get(position--).getHeight();
        }
        if (heightUsed > height) {
            ++position;
        }
        return position + 1;
    }

    public int getLastScrollPositionH() {

        return Math.max(getInternalWidth() - getContentWidth(), 0);
    }

    public int getSelectedIndex() {

        return selectedIndex;
    }

    public int getIndexOf(Object value) {

        for (int i = 0; i < elements.size(); ++i) {
            if (elements.get(i).getValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public IListBoxElement getSelectedElement() {

        if (selectedIndex == -1 || selectedIndex >= elements.size()) {
            return null;
        }
        return elements.get(selectedIndex);
    }

    public void setSelectedIndex(int index) {

        if (index >= -1 && index != selectedIndex && index < elements.size()) {
            selectedIndex = index;
            onSelectionChanged(selectedIndex, getSelectedElement());
        }
    }

    public IListBoxElement getElement(int index) {

        return elements.get(index);
    }

    public int getElementCount() {

        return elements.size();
    }

    public void scrollToV(int index) {

        if (index >= 0 && index < elements.size()) {
            firstIndexDisplayed = index;
        }
    }

    public void scrollToH(int index) {

        if (index >= 0 && index <= getLastScrollPositionH()) {
            scrollHoriz = index;
        }
    }

    protected void onElementClicked(IListBoxElement element) {

    }

    protected void onScrollV(int newStartIndex) {

    }

    protected void onScrollH(int newStartIndex) {

    }

    protected void onSelectionChanged(int newIndex, IListBoxElement newElement) {

    }

}
