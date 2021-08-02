package cofh.core.client.gui.element.panel;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;

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
    protected void drawForeground(MatrixStack matrixStack) {

        drawPanelIcon(matrixStack, icon);
        if (!fullyOpen) {
            return;
        }
        getFontRenderer().drawStringWithShadow(matrixStack, localize(resource), sideOffset() + 20, 6, headerColor);

        if (curAmt.getAsInt() > -1) {
            getFontRenderer().drawStringWithShadow(matrixStack, localize(curDesc) + ":", sideOffset() + 6, 18, subheaderColor);
            getFontRenderer().drawString(matrixStack, curAmt.getAsInt() + " " + curUnit, sideOffset() + 14, 30, textColor);
        }
        if (maxAmt.getAsInt() > -1) {
            getFontRenderer().drawStringWithShadow(matrixStack, localize(maxDesc) + ":", sideOffset() + 6, 42, subheaderColor);
            getFontRenderer().drawString(matrixStack, maxAmt.getAsInt() + " " + maxUnit, sideOffset() + 14, 54, textColor);
        }
        if (efficiency.getAsDouble() > -1) {
            getFontRenderer().drawStringWithShadow(matrixStack, localize("info.cofh.efficiency") + ":", sideOffset() + 6, 66, subheaderColor);
            getFontRenderer().drawString(matrixStack, DF0.format(efficiency.getAsDouble() * 100) + "%", sideOffset() + 14, 78, textColor);
        }
        RenderHelper.resetColor();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(getTextComponent(curAmt.getAsInt() + " " + curUnit));
        }
    }

}
