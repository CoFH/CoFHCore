package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.element.ElementBase;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

import static cofh.lib.util.constants.Constants.PATH_ELEMENTS;

public abstract class PanelBase extends ElementBase {

    public static final ResourceLocation DEFAULT_TEXTURE_LEFT = new ResourceLocation(PATH_ELEMENTS + "panel_left.png");
    public static final ResourceLocation DEFAULT_TEXTURE_RIGHT = new ResourceLocation(PATH_ELEMENTS + "panel_right.png");

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public static int panelExpandSpeed = 8;

    public boolean open;
    protected boolean fullyOpen;
    public int side;

    protected int headerColor = 0xe1c92f;
    protected int subheaderColor = 0xaaafb8;
    protected int textColor = 0x000000;
    protected int backgroundColor = 0xffffff;

    protected int shiftX = 0;
    protected int shiftY = 0;

    protected int mX = 0;
    protected int mY = 0;

    protected int minWidth = 22;
    protected int maxWidth = 100;

    protected int minHeight = 22;
    protected int maxHeight = 92;

    private final ArrayList<ElementBase> elements = new ArrayList<>();

    public PanelBase(IGuiAccess gui) {

        this(gui, RIGHT);
    }

    public PanelBase(IGuiAccess gui, int sideIn) {

        super(gui);
        side = sideIn;
        width = minWidth;
        height = minHeight;
        texture = side == LEFT ? DEFAULT_TEXTURE_LEFT : DEFAULT_TEXTURE_RIGHT;
    }

    public PanelBase setBackgroundColor(int backgroundColor) {

        this.backgroundColor = backgroundColor;
        return this;
    }

    public boolean intersectsWith(double mouseX, double mouseY, int shiftX, int shiftY) {

        if (side == LEFT) {
            return mouseX <= shiftX && mouseX >= shiftX - width && mouseY >= shiftY && mouseY <= shiftY + height;
        } else {
            return mouseX >= shiftX && mouseX <= shiftX + width && mouseY >= shiftY && mouseY <= shiftY + height;
        }
    }

    protected void drawPanelIcon(MatrixStack matrixStack, TextureAtlasSprite iconName) {

        gui.drawIcon(matrixStack, iconName, sideOffset(), 3);
    }

    protected void drawForeground(MatrixStack matrixStack) {

    }

    protected void drawBackground(MatrixStack matrixStack) {

        float colorR = (backgroundColor >> 16 & 255) / 255.0F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F;
        float colorB = (backgroundColor & 255) / 255.0F;

        RenderSystem.color4f(colorR, colorG, colorB, 1.0F);

        RenderHelper.bindTexture(texture);

        gui.drawTexturedModalRect(0, 4, 0, 256 - height + 4, 4, height - 4);
        gui.drawTexturedModalRect(4, 0, 256 - width + 4, 0, width - 4, 4);
        gui.drawTexturedModalRect(0, 0, 0, 0, 4, 4);
        gui.drawTexturedModalRect(4, 4, 256 - width + 4, 256 - height + 4, width - 4, height - 4);

        RenderHelper.resetColor();
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        mouseX -= this.posX();
        mouseY -= this.posY();

        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.posX(), this.posY(), 0.0F);

        drawBackground(matrixStack);

        if (fullyOpen) {
            for (ElementBase element : elements) {
                if (element.visible()) {
                    element.drawBackground(matrixStack, mouseX, mouseY);
                }
            }
        }
        RenderSystem.popMatrix();
    }

    @Override
    public void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        mouseX -= this.posX();
        mouseY -= this.posY();

        RenderSystem.pushMatrix();
        RenderSystem.translatef(this.posX(), this.posY(), 0.0F);

        drawForeground(matrixStack);

        if (fullyOpen) {
            for (ElementBase element : elements) {
                if (element.visible()) {
                    element.drawForeground(matrixStack, mouseX, mouseY);
                }
            }
        }
        RenderSystem.popMatrix();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

        tooltipList.addAll(tooltip.create(this, mouseX, mouseY));

        mouseX -= this.posX();
        mouseY -= this.posY();

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled() || !c.intersectsWith(mouseX, mouseY)) {
                continue;
            }
            c.addTooltip(tooltipList, mouseX, mouseY);
        }
    }

    @Override
    public void update(int mouseX, int mouseY) {

        super.update(mouseX, mouseY);

        mX = mouseX - posX();
        mY = mouseY - posY();

        updateElements();
    }

    public void setShift(int x, int y) {

        updateElements();

        shiftX = x;
        shiftY = y;
    }

    public void updateSize() {

        if (open && width < maxWidth) {
            width += panelExpandSpeed;
        } else if (!open && width > minWidth) {
            width -= panelExpandSpeed;
        }
        if (width > maxWidth) {
            width = maxWidth;
        } else if (width < minWidth) {
            width = minWidth;
        }
        if (open && height < maxHeight) {
            height += panelExpandSpeed;
        } else if (!open && height > minHeight) {
            height -= panelExpandSpeed;
        }
        if (height > maxHeight) {
            height = maxHeight;
        } else if (height < minHeight) {
            height = minHeight;
        }
        if (!fullyOpen && open && width == maxWidth && height == maxHeight) {
            setFullyOpen();
        }
    }

    public void setFullyOpen() {

        open = true;
        fullyOpen = true;
        width = maxWidth;
        height = maxHeight;
    }

    public void toggleOpen() {

        if (open) {
            open = false;
            fullyOpen = false;
            if (side == LEFT) {
                PanelTracker.setOpenedLeft(null);
            } else {
                PanelTracker.setOpenedRight(null);
            }
        } else {
            open = true;
            if (side == LEFT) {
                PanelTracker.setOpenedLeft(this.getClass());
            } else {
                PanelTracker.setOpenedRight(this.getClass());
            }
        }
    }

    protected final void updateElements() {

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (c.visible() && c.enabled()) {
                c.update(mX, mY);
            }
        }
    }

    public final Rectangle2d getBoundsOnScreen() {

        return new Rectangle2d(posX() + guiLeft(), posY() + guiTop(), visible() ? width : 0, visible() ? height : 0);
    }

    @SuppressWarnings("unchecked")
    protected <T> T addElement(ElementBase element) {

        elements.add(element.setVisible(() -> fullyOpen).setOffsets(this::posX, () -> this.posY()));
        return (T) element;
    }

    // region HELPERS
    @Override
    public int posX() {

        return side == LEFT ? super.posX() - width : super.posX();
    }

    /**
     * Corrects for shadowing differences in panels to ensure that they always look nice - used in font rendering, typically.
     */
    protected int sideOffset() {

        return (side == LEFT ? 4 : 2);
    }
    // endregion

    // region CALLBACKS
    @Override
    public boolean keyTyped(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled()) {
                continue;
            }
            if (c.keyTyped(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
                return true;
            }
        }
        return super.keyTyped(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    /**
     * @return Whether the panel should stay open or not.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        mouseX -= this.posX();
        mouseY -= this.posY();

        boolean shouldStayOpen = false;

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled() || !c.intersectsWith(mouseX, mouseY)) {
                continue;
            }
            shouldStayOpen = true;

            if (c.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }
        return shouldStayOpen;
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY) {

        mouseX -= this.posX();
        mouseY -= this.posY();

        for (int i = elements.size(); i-- > 0; ) {
            ElementBase c = elements.get(i);
            if (!c.visible() || !c.enabled()) { // no bounds checking on mouseUp events
                continue;
            }
            c.mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseWheel(double mouseX, double mouseY, double movement) {

        mouseX -= this.posX();
        mouseY -= this.posY();

        if (movement != 0) {
            for (int i = elements.size(); i-- > 0; ) {
                ElementBase c = elements.get(i);
                if (!c.visible() || !c.enabled() || !c.intersectsWith(mouseX, mouseY)) {
                    continue;
                }
                if (c.mouseWheel(mouseX, mouseY, movement)) {
                    return true;
                }
            }
        }
        return true;
    }
    // endregion
}
