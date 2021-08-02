package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.CoreTextures;
import cofh.core.client.gui.element.ElementConditionalLayered;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.util.control.IReconfigurable;
import cofh.lib.util.control.ITransferControllable;
import cofh.lib.util.helpers.BlockHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.function.Supplier;

import static cofh.core.client.gui.CoreTextures.*;
import static cofh.lib.util.helpers.SoundHelper.playClickSound;
import static cofh.lib.util.helpers.StringHelper.localize;

public class ConfigPanel extends PanelBase {

    public static int defaultSide = RIGHT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColor = 0x226688;

    private final IReconfigurable myReconfig;
    private final ITransferControllable myTransfer;
    private final Supplier<Direction> myFacing;

    private boolean allowFacingConfig = false;

    public ConfigPanel(IGuiAccess gui, IReconfigurable reconfig, Supplier<Direction> facingSup) {

        this(gui, defaultSide, null, reconfig, facingSup);
    }

    public ConfigPanel(IGuiAccess gui, ITransferControllable transfer, IReconfigurable reconfig, Supplier<Direction> facingSup) {

        this(gui, defaultSide, transfer, reconfig, facingSup);
    }

    protected ConfigPanel(IGuiAccess gui, int sideIn, ITransferControllable transfer, IReconfigurable reconfig, Supplier<Direction> facingSup) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = transfer == null ? 100 : 112;
        myReconfig = reconfig;
        myTransfer = transfer;
        myFacing = facingSup;

        this.setVisible(myReconfig::isReconfigurable);
    }

    public ConfigPanel allowFacingConfig(boolean allowFacingConfig) {

        this.allowFacingConfig = allowFacingConfig;
        return this;
    }

    public ConfigPanel addConditionals(ElementConditionalLayered... c) {

        if (c.length != 6) {
            return this;
        }
        int xOffset = myTransfer != null ? 12 : 0;

        c[0].setPosition(40 + xOffset, 24);
        c[1].setPosition(20 + xOffset, 44);
        c[2].setPosition(40 + xOffset, 44);
        c[3].setPosition(60 + xOffset, 44);
        c[4].setPosition(40 + xOffset, 64);
        c[5].setPosition(60 + xOffset, 64);

        // This is done explicitly to use the addElement method which appends a visibility supplier.
        for (int i = 0; i < 6; ++i) {
            addElement(c[i]);
        }
        return this;
    }

    @Override
    protected void drawForeground(MatrixStack matrixStack) {

        drawPanelIcon(matrixStack, CoreTextures.ICON_CONFIG);
        if (!fullyOpen) {
            return;
        }
        getFontRenderer().drawStringWithShadow(matrixStack, localize("info.cofh.configuration"), sideOffset() + 18, 6, headerColor);

        if (myTransfer != null) {
            if (myTransfer.hasTransferIn()) {
                gui.drawIcon(matrixStack, myTransfer.getTransferIn() ? ICON_BUTTON_HIGHLIGHT : ICON_BUTTON, 8, 34);
            } else {
                gui.drawIcon(matrixStack, ICON_BUTTON_INACTIVE, 8, 34);
            }
            if (myTransfer.hasTransferOut()) {
                gui.drawIcon(matrixStack, myTransfer.getTransferOut() ? ICON_BUTTON_HIGHLIGHT : ICON_BUTTON, 8, 54);
            } else {
                gui.drawIcon(matrixStack, ICON_BUTTON_INACTIVE, 8, 54);
            }
            gui.drawIcon(matrixStack, ICON_INPUT, 8, 34);
            gui.drawIcon(matrixStack, ICON_OUTPUT, 8, 54);
        }
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

        if (myTransfer == null) {
            gui.drawTexturedModalRect(16, 20, 16, 20, 64, 64);
        } else {
            gui.drawTexturedModalRect(28, 20, 16, 20, 64, 64);
            gui.drawTexturedModalRect(6, 32, 16, 20, 20, 40);
        }
        RenderHelper.resetColor();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(new TranslationTextComponent("info.cofh.configuration"));
            return;
        }
        if (myTransfer == null) {
            return;
        }
        int x = mouseX - this.posX();
        int y = mouseY - this.posY();

        if (8 <= x && x < 24 && 34 <= y && y < 50) {
            if (myTransfer.hasTransferIn()) {
                tooltipList.add(myTransfer.getTransferIn() ? new TranslationTextComponent("info.cofh.transfer_in_enabled") : new TranslationTextComponent("info.cofh.transfer_in_disabled"));
            } else {
                tooltipList.add(new TranslationTextComponent("info.cofh.transfer_in_unavailable"));
            }
        } else if (8 <= x && x < 24 && 54 <= y && y < 68) {
            if (myTransfer.hasTransferOut()) {
                tooltipList.add(myTransfer.getTransferOut() ? new TranslationTextComponent("info.cofh.transfer_out_enabled") : new TranslationTextComponent("info.cofh.transfer_out_disabled"));
            } else {
                tooltipList.add(new TranslationTextComponent("info.cofh.transfer_out_unavailable"));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!fullyOpen) {
            return false;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        return myTransfer != null ? clickTransfer(x, y, mouseButton) : clickNoTransfer(x, y, mouseButton);
    }

    // region HELPERS
    protected boolean clickNoTransfer(double x, double y, int mouseButton) {

        if (x < 16 || x >= 80 || y < 20 || y >= 84) {
            return false;
        }
        Direction facing = myFacing.get();
        if (40 <= x && x < 56 && 24 <= y && y < 40) {
            handleSideChange(BlockHelper.above(facing), mouseButton);
        } else if (20 <= x && x < 36 && 44 <= y && y < 60) {
            handleSideChange(BlockHelper.left(facing), mouseButton);
        } else if (40 <= x && x < 56 && 44 <= y && y < 60) {
            handleSideChange(facing, mouseButton);
        } else if (60 <= x && x < 76 && 44 <= y && y < 60) {
            handleSideChange(BlockHelper.right(facing), mouseButton);
        } else if (40 <= x && x < 56 && 64 <= y && y < 80) {
            handleSideChange(BlockHelper.below(facing), mouseButton);
        } else if (60 <= x && x < 76 && 64 <= y && y < 80) {
            handleSideChange(BlockHelper.opposite(facing), mouseButton);
        }
        return true;
    }

    protected boolean clickTransfer(double x, double y, int mouseButton) {

        if (x < 4 || x >= 92 || y < 20 || y >= 84) {
            return false;
        }
        if (8 <= x && x < 24 && 34 <= y && y < 50) {
            handleTransferChange(true);
        } else if (8 <= x && x < 24 && 54 <= y && y < 68) {
            handleTransferChange(false);
        }
        Direction facing = myFacing.get();
        if (52 <= x && x < 68 && 24 <= y && y < 40) {
            handleSideChange(BlockHelper.above(facing), mouseButton);
        } else if (32 <= x && x < 48 && 44 <= y && y < 60) {
            handleSideChange(BlockHelper.left(facing), mouseButton);
        } else if (52 <= x && x < 68 && 44 <= y && y < 60) {
            handleSideChange(facing, mouseButton);
        } else if (72 <= x && x < 88 && 44 <= y && y < 60) {
            handleSideChange(BlockHelper.right(facing), mouseButton);
        } else if (52 <= x && x < 68 && 64 <= y && y < 80) {
            handleSideChange(BlockHelper.below(facing), mouseButton);
        } else if (72 <= x && x < 88 && 64 <= y && y < 80) {
            handleSideChange(BlockHelper.opposite(facing), mouseButton);
        }
        return true;
    }

    protected void handleTransferChange(boolean input) {

        if (myTransfer == null) { // Should absolutely NEVER happen.
            return;
        }
        if (input) {
            if (myTransfer.hasTransferIn()) {
                myTransfer.setControl(!myTransfer.getTransferIn(), myTransfer.getTransferOut());
                playClickSound(myTransfer.getTransferIn() ? 0.6F : 0.8F);
            }
        } else {
            if (myTransfer.hasTransferOut()) {
                myTransfer.setControl(myTransfer.getTransferIn(), !myTransfer.getTransferOut());
                playClickSound(myTransfer.getTransferOut() ? 0.6F : 0.8F);
            }
        }
    }

    protected void handleSideChange(Direction side, int mouseButton) {

        Direction facing = myFacing.get();

        if (Screen.hasShiftDown()) {
            if (side == facing) {
                if (myReconfig.clearAllSides()) {
                    playClickSound(0.2F);
                }
            } else if (myReconfig.setSideConfig(side, IReconfigurable.SideConfig.SIDE_NONE)) {
                playClickSound(0.4F);
            }
            return;
        }
        if (side == facing && !allowFacingConfig) {
            return;
        }
        if (mouseButton == 0) {
            if (myReconfig.nextSideConfig(side)) {
                playClickSound(0.8F);
            }
        } else if (mouseButton == 1) {
            if (myReconfig.prevSideConfig(side)) {
                playClickSound(0.6F);
            }
        }
    }
    // endregion
}
