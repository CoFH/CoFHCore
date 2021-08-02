package cofh.core.client.gui.element.panel;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

import static cofh.core.client.gui.CoreTextures.*;

public abstract class PanelScrolledText extends PanelBase {

    protected List<IReorderingProcessor> myText;
    protected int firstLine = 0;
    protected int maxFirstLine;
    protected int numLines;

    protected boolean scrollable;

    public PanelScrolledText(IGuiAccess gui, int sideIn, String info) {

        super(gui, sideIn);

        maxHeight = 92;

        myText = getFontRenderer().trimStringToWidth(new StringTextComponent(info), maxWidth - 16);
        numLines = Math.min(myText.size(), (maxHeight - 24) / getFontRenderer().FONT_HEIGHT);
        maxFirstLine = myText.size() - numLines;
        scrollable = maxFirstLine > 0;
    }

    public abstract TextureAtlasSprite getIcon();

    public abstract ITextComponent getTitle();

    @Override
    public void drawForeground(MatrixStack matrixStack) {

        drawPanelIcon(matrixStack, getIcon());
        if (!fullyOpen) {
            return;
        }
        if (scrollable) {
            if (firstLine > 0) {
                gui.drawIcon(matrixStack, ICON_ARROW_UP, sideOffset() + maxWidth - 20, 16);
            } else {
                gui.drawIcon(matrixStack, ICON_ARROW_UP_INACTIVE, sideOffset() + maxWidth - 20, 16);
            }
            if (firstLine < maxFirstLine) {
                gui.drawIcon(matrixStack, ICON_ARROW_DOWN, sideOffset() + maxWidth - 20, 76);
            } else {
                gui.drawIcon(matrixStack, ICON_ARROW_DOWN_INACTIVE, sideOffset() + maxWidth - 20, 76);
            }
        }
        getFontRenderer().drawStringWithShadow(matrixStack, getTitle().getString(), sideOffset() + 18, 6, headerColor);
        for (int i = firstLine; i < firstLine + numLines; ++i) {
            getFontRenderer().func_238422_b_(matrixStack, myText.get(i), sideOffset() + 2, 20 + (i - firstLine) * getFontRenderer().FONT_HEIGHT, textColor);
        }
        RenderHelper.resetColor();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

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
