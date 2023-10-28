package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import static cofh.lib.util.helpers.StringHelper.*;

public class ResourcePanel extends PanelBase {

    public static int defaultSide = LEFT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColorOut = 0xd0650b;
    public static int defaultBackgroundColorIn = 0x0a76d0;

    private TextureAtlasSprite icon;

    private String resource = "";

    private IntSupplier curAmt = () -> -1;
    private String curDesc = "";
    private String curUnit = "";

    private IntSupplier maxAmt = () -> -1;
    private String maxDesc = "";
    private String maxUnit = "";

    private DoubleSupplier efficiency = () -> -1;

    public ResourcePanel(IGuiAccess gui) {

        this(gui, defaultSide);
    }

    protected ResourcePanel(IGuiAccess gui, int sideIn) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColorIn;

        maxHeight = 92;
        maxWidth = 100;

        setVisible(() -> !resource.isEmpty());
    }

    public ResourcePanel setResource(TextureAtlasSprite icon, String resource, boolean producer) {

        this.icon = icon;
        this.resource = resource;
        this.backgroundColor = producer ? defaultBackgroundColorOut : defaultBackgroundColorIn;
        return this;
    }

    public ResourcePanel setCurrent(IntSupplier curAmt, String curDesc, String curUnit) {

        this.curAmt = curAmt;
        this.curDesc = curDesc;
        this.curUnit = curUnit;
        return this;
    }

    public ResourcePanel setMax(IntSupplier maxAmt, String maxDesc, String maxUnit) {

        this.maxAmt = maxAmt;
        this.maxDesc = maxDesc;
        this.maxUnit = maxUnit;
        return this;
    }

    public ResourcePanel setEfficiency(DoubleSupplier efficiency) {

        this.efficiency = efficiency;
        return this;
    }

    @Override
    protected void drawForeground(GuiGraphics pGuiGraphics) {

        drawPanelIcon(pGuiGraphics, icon);
        if (!fullyOpen) {
            return;
        }
        pGuiGraphics.drawString(fontRenderer(), localize(resource), sideOffset() + 20, 6, headerColor, true);

        if (curAmt.getAsInt() >= 0) {
            pGuiGraphics.drawString(fontRenderer(), localize(curDesc) + ":", sideOffset() + 6, 18, subheaderColor, true);
            pGuiGraphics.drawString(fontRenderer(), curAmt.getAsInt() + " " + localize(curUnit), sideOffset() + 14, 30, textColor);
        }
        if (maxAmt.getAsInt() >= 0) {
            pGuiGraphics.drawString(fontRenderer(), localize(maxDesc) + ":", sideOffset() + 6, 42, subheaderColor, true);
            pGuiGraphics.drawString(fontRenderer(), maxAmt.getAsInt() + " " + localize(maxUnit), sideOffset() + 14, 54, textColor);
        }
        if (efficiency.getAsDouble() >= 0) {
            pGuiGraphics.drawString(fontRenderer(), localize("info.cofh.efficiency") + ":", sideOffset() + 6, 66, subheaderColor, true);
            pGuiGraphics.drawString(fontRenderer(), DF0.format(efficiency.getAsDouble() * 100) + "%", sideOffset() + 14, 78, textColor);
        }
        RenderHelper.resetShaderColor();
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(getTextComponent(curAmt.getAsInt() + " " + localize(curUnit)));
        }
    }

}
