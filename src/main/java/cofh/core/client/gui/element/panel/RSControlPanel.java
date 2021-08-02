package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.CoreTextures;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.util.control.IRedstoneControllable;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static cofh.core.client.gui.CoreTextures.*;
import static cofh.lib.util.control.IRedstoneControllable.ControlMode.*;
import static cofh.lib.util.helpers.SoundHelper.playClickSound;
import static cofh.lib.util.helpers.StringHelper.localize;

public class RSControlPanel extends PanelBase {

    public static int defaultSide = RIGHT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColor = 0xd0230a;

    private final IRedstoneControllable myRSControllable;

    public RSControlPanel(IGuiAccess gui, IRedstoneControllable rsControllable) {

        this(gui, defaultSide, rsControllable);
    }

    protected RSControlPanel(IGuiAccess gui, int sideIn, IRedstoneControllable rsControllable) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 112;
        myRSControllable = rsControllable;

        this.setVisible(myRSControllable::isControllable);
    }

    // TODO: Fully support new Redstone Control system.
    @Override
    protected void drawForeground(MatrixStack matrixStack) {

        drawPanelIcon(matrixStack, CoreTextures.ICON_REDSTONE_ON);
        if (!fullyOpen) {
            return;
        }
        getFontRenderer().drawStringWithShadow(matrixStack, localize("info.cofh.redstone_control"), sideOffset() + 18, 6, headerColor);
        getFontRenderer().drawStringWithShadow(matrixStack, localize("info.cofh.control_status") + ":", sideOffset() + 6, 42, subheaderColor);
        getFontRenderer().drawStringWithShadow(matrixStack, localize("info.cofh.signal_required") + ":", sideOffset() + 6, 66, subheaderColor);

        gui.drawIcon(matrixStack, ICON_BUTTON, 28, 20);
        gui.drawIcon(matrixStack, ICON_BUTTON, 48, 20);
        gui.drawIcon(matrixStack, ICON_BUTTON, 68, 20);

        switch (myRSControllable.getMode()) {
            case DISABLED:
                gui.drawIcon(matrixStack, ICON_BUTTON_HIGHLIGHT, 28, 20);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.disabled"), sideOffset() + 14, 54, textColor);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.ignored"), sideOffset() + 14, 78, textColor);
                break;
            case LOW:
                gui.drawIcon(matrixStack, ICON_BUTTON_HIGHLIGHT, 48, 20);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.enabled"), sideOffset() + 14, 54, textColor);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.low"), sideOffset() + 14, 78, textColor);
                break;
            case HIGH:
                gui.drawIcon(matrixStack, ICON_BUTTON_HIGHLIGHT, 68, 20);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.enabled"), sideOffset() + 14, 54, textColor);
                getFontRenderer().drawString(matrixStack, localize("info.cofh.high"), sideOffset() + 14, 78, textColor);
                break;
            default:
        }
        gui.drawIcon(matrixStack, ICON_REDSTONE_OFF, 28, 20);
        gui.drawIcon(matrixStack, ICON_RS_TORCH_OFF, 48, 20);
        gui.drawIcon(matrixStack, ICON_RS_TORCH_ON, 68, 20);

        RenderHelper.resetColor();
    }

    @Override
    protected void drawBackground(MatrixStack matrixStack) {

        super.drawBackground(matrixStack);

        if (!fullyOpen) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        RenderSystem.color4f(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(24, 16, 16, 20, 64, 24);
        RenderHelper.resetColor();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(new TranslationTextComponent("info.cofh.redstone_control"));

            switch (myRSControllable.getMode()) {
                case DISABLED:
                    tooltipList.add(new TranslationTextComponent("info.cofh.disabled").mergeStyle(TextFormatting.YELLOW));
                    break;
                case LOW:
                    tooltipList.add(new TranslationTextComponent("info.cofh.low").mergeStyle(TextFormatting.YELLOW));
                    break;
                case HIGH:
                    tooltipList.add(new TranslationTextComponent("info.cofh.high").mergeStyle(TextFormatting.YELLOW));
                    break;
                default:
            }
            tooltipList.add(new TranslationTextComponent("info.cofh.current_signal", myRSControllable.getPower())
                    .mergeStyle(myRSControllable.getMode() == DISABLED
                            ? TextFormatting.YELLOW
                            : myRSControllable.getState()
                            ? TextFormatting.GREEN
                            : TextFormatting.RED));
            return;
        }
        int x = mouseX - this.posX();
        int y = mouseY - this.posY();

        if (28 <= x && x < 44 && 20 <= y && y < 36) {
            tooltipList.add(new TranslationTextComponent("info.cofh.ignored"));
        } else if (48 <= x && x < 64 && 20 <= y && y < 36) {
            tooltipList.add(new TranslationTextComponent("info.cofh.low"));
        } else if (68 <= x && x < 84 && 20 <= y && y < 36) {
            tooltipList.add(new TranslationTextComponent("info.cofh.high"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!fullyOpen) {
            return false;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        if (x < 24 || x >= 88 || y < 16 || y >= 40) {
            return false;
        }
        if (28 <= x && x < 44 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != DISABLED) {
                myRSControllable.setControl(0, DISABLED);
                playClickSound(0.4F);
            }
        } else if (48 <= x && x < 64 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != LOW) {
                myRSControllable.setControl(0, LOW);
                playClickSound(0.6F);
            }
        } else if (68 <= x && x < 84 && 20 <= y && y < 36) {
            if (myRSControllable.getMode() != HIGH) {
                myRSControllable.setControl(0, HIGH);
                playClickSound(0.8F);
            }
        }
        return true;
    }

}
