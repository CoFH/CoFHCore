package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static cofh.core.client.gui.CoreTextures.*;

public abstract class PanelScrolledText extends PanelBase {

    protected List<FormattedCharSequence> myText;
    protected int firstLine = 0;
    protected int maxFirstLine;
    protected int numLines;

    protected boolean scrollable;

    public PanelScrolledText(IGuiAccess gui, int sideIn, String info) {

        super(gui, sideIn);

        maxHeight = 92;

        myText = fontRenderer().split(Component.literal(info), maxWidth - 16);
        numLines = Math.min(myText.size(), (maxHeight - 24) / fontRenderer().lineHeight);
        maxFirstLine = myText.size() - numLines;
        scrollable = maxFirstLine > 0;
    }

    public abstract TextureAtlasSprite getIcon();

    public abstract Component getTitle();

    @Override
    public void drawForeground(GuiGraphics pGuiGraphics) {

        drawPanelIcon(pGuiGraphics, getIcon());
        if (!fullyOpen) {
            return;
        }
        if (scrollable) {
            if (firstLine > 0) {
                gui.drawIcon(pGuiGraphics, ICON_ARROW_UP, sideOffset() + maxWidth - 20, 16);
            } else {
                gui.drawIcon(pGuiGraphics, ICON_ARROW_UP_INACTIVE, sideOffset() + maxWidth - 20, 16);
            }
            if (firstLine < maxFirstLine) {
                gui.drawIcon(pGuiGraphics, ICON_ARROW_DOWN, sideOffset() + maxWidth - 20, 76);
            } else {
                gui.drawIcon(pGuiGraphics, ICON_ARROW_DOWN_INACTIVE, sideOffset() + maxWidth - 20, 76);
            }
        }
        pGuiGraphics.drawString(fontRenderer(), getTitle().getString(), sideOffset() + 18, 6, headerColor, true);
        for (int i = firstLine; i < firstLine + numLines; ++i) {
            pGuiGraphics.drawString(fontRenderer(), myText.get(i), sideOffset() + 2, 20 + (i - firstLine) * fontRenderer().lineHeight, textColor);
        }
        RenderHelper.resetShaderColor();
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(getTitle());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        double shiftedMouseX = mouseX - this.posX();
        double shiftedMouseY = mouseY - this.posY();

        if (!fullyOpen) {
            return false;
        }
        if (!scrollable || shiftedMouseY < 16 || shiftedMouseX < maxWidth - 16) {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (shiftedMouseY < 32) {
            firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
        } else if (shiftedMouseY > maxHeight - 16) {
            firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
        }
        return true;
    }

    @Override
    public boolean mouseWheel(double mouseX, double mouseY, double movement) {

        if (!fullyOpen) {
            return false;
        }
        if (movement > 0) {
            firstLine = MathHelper.clamp(firstLine - 1, 0, maxFirstLine);
            return true;
        } else if (movement < 0) {
            firstLine = MathHelper.clamp(firstLine + 1, 0, maxFirstLine);
            return true;
        }
        return false;
    }

}
