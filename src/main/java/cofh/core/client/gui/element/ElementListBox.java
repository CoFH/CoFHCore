package cofh.core.client.gui.element;

import cofh.core.client.gui.GuiColor;
import cofh.core.client.gui.IGuiAccess;
import cofh.core.client.gui.element.listbox.IListBoxElement;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
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

    protected int _marginTop = 2;
    protected int _marginLeft = 2;
    protected int _marginRight = 2;
    protected int _marginBottom = 2;

    protected final List<IListBoxElement> _elements = new LinkedList<>();

    protected int _firstIndexDisplayed;
    protected int _selectedIndex;
    protected int scrollHoriz;

    public ElementListBox(IGuiAccess containerScreen, int x, int y, int width, int height) {

        super(containerScreen, x, y, width, height);
    }

    public void add(IListBoxElement element) {

        _elements.add(element);
    }

    public void add(Collection<? extends IListBoxElement> elements) {

        _elements.addAll(elements);
    }

    public void remove(IListBoxElement element) {

        int e = _elements.indexOf(element);
        if (_elements.remove(element)) {
            if (e < _firstIndexDisplayed) {
                --_firstIndexDisplayed;
            }
            if (e < _selectedIndex) {
                --_selectedIndex;
            }
        }
    }

    public void removeAt(int index) {

        _firstIndexDisplayed = scrollHoriz = 0;
        _selectedIndex = -1;
        _elements.remove(index);
    }

    public void removeAll() {

        _elements.clear();
    }

    public int getInternalWidth() {

        int width = 0;
        for (int i = 0; i < _elements.size(); ++i) {
            width = Math.max(_elements.get(i).getWidth(), width);
        }
        return width;
    }

    public int getInternalHeight() {

        int height = 0;
        for (int i = 0; i < _elements.size(); ++i) {
            height += _elements.get(i).getHeight();
        }
        return height;
    }

    public int getContentWidth() {

        return width - _marginLeft - _marginRight;
    }

    public int getContentHeight() {

        return height - _marginTop - _marginBottom;
    }

    public int getContentTop() {

        return posY() + _marginTop;
    }

    public int getContentLeft() {

        return posX() + _marginLeft;
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
    public void drawBackground(PoseStack poseStack, int mouseX, int mouseY) {

        drawColoredModalRect(poseStack, posX() - 1, posY() - 1, posX() + width + 1, posY() + height + 1, borderColor);
        drawColoredModalRect(poseStack, posX(), posY(), posX() + width, posY() + height, backgroundColor);
    }

    @Override
    public void drawForeground(PoseStack matrixStack, int mouseX, int mouseY) {

        int heightDrawn = 0;
        int nextElement = _firstIndexDisplayed;

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        RenderHelper.drawStencil(matrixStack, getContentLeft(), getContentTop(), getContentRight(), getContentBottom(), 1);

        matrixStack.pushPose();
        matrixStack.translate(-scrollHoriz, 0, 0);

        int e = _elements.size();
        while (nextElement < e && heightDrawn <= getContentHeight()) {
            heightDrawn += drawElement(matrixStack, nextElement, getContentLeft(), getContentTop() + heightDrawn);
            ++nextElement;
        }
        matrixStack.popPose();
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    protected int drawElement(PoseStack matrixStack, int elementIndex, int x, int y) {

        IListBoxElement element = _elements.get(elementIndex);
        if (elementIndex == _selectedIndex) {
            element.draw(matrixStack, this, x, y, selectedLineColor, selectedTextColor);
        } else {
            element.draw(matrixStack, this, x, y, backgroundColor, textColor);
        }

        return element.getHeight();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        int heightChecked = 0;
        for (int i = _firstIndexDisplayed; i < _elements.size(); ++i) {
            if (heightChecked > getContentHeight()) {
                break;
            }
            int elementHeight = _elements.get(i).getHeight();
            if (getContentTop() + heightChecked <= mouseY && getContentTop() + heightChecked + elementHeight >= mouseY) {
                setSelectedIndex(i);
                onElementClicked(_elements.get(i));
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
        for (int i = _firstIndexDisplayed; i < _elements.size(); ++i) {
            if (heightDisplayed + _elements.get(i).getHeight() > height) {
                break;
            }
            heightDisplayed += _elements.get(i).getHeight();
            ++elementsDisplayed;
        }
        if (_firstIndexDisplayed + elementsDisplayed < _elements.size()) {
            ++_firstIndexDisplayed;
        }
        onScrollV(_firstIndexDisplayed);
    }

    public void scrollUp() {

        if (_firstIndexDisplayed > 0) {
            --_firstIndexDisplayed;
        }
        onScrollV(_firstIndexDisplayed);
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

        int position = _elements.size() - 1;
        if (position < 0) {
            return 0;
        }
        int heightUsed = 0;

        while (position >= 0 && heightUsed < height) {
            heightUsed += _elements.get(position--).getHeight();
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

        return _selectedIndex;
    }

    public int getIndexOf(Object value) {

        for (int i = 0; i < _elements.size(); ++i) {
            if (_elements.get(i).getValue().equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public IListBoxElement getSelectedElement() {

        if (_selectedIndex == -1 || _selectedIndex >= _elements.size()) {
            return null;
        }
        return _elements.get(_selectedIndex);
    }

    public void setSelectedIndex(int index) {

        if (index >= -1 && index != _selectedIndex && index < _elements.size()) {
            _selectedIndex = index;
            onSelectionChanged(_selectedIndex, getSelectedElement());
        }
    }

    public IListBoxElement getElement(int index) {

        return _elements.get(index);
    }

    public int getElementCount() {

        return _elements.size();
    }

    public void scrollToV(int index) {

        if (index >= 0 && index < _elements.size()) {
            _firstIndexDisplayed = index;
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
