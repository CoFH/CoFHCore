package cofh.core.client.gui.element.panel;

import cofh.core.client.gui.CoreTextures;
import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.filter.IFilter;
import cofh.core.util.filter.IFilterable;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

import java.util.List;

import static cofh.lib.util.helpers.StringHelper.localize;

public class FilterPanel extends PanelBase {

    public static int defaultSide = LEFT;
    public static int defaultHeaderColor = 0xe1c92f;
    public static int defaultSubHeaderColor = 0xaaafb8;
    public static int defaultTextColor = 0x101010;
    public static int defaultBackgroundColor = 0x8a3280;

    private final int slotsBorderX1 = 18;
    private final int slotsBorderX2 = slotsBorderX1 + 60;
    private final int slotsBorderY1 = 20;
    private final int slotsBorderY2 = slotsBorderY1 + 60;

    private final IFilterable myFilterable;
    private IFilter myFilter;

    public FilterPanel(IGuiAccess gui, IFilterable filterable) {

        this(gui, defaultSide, filterable);
    }

    protected FilterPanel(IGuiAccess gui, int sideIn, IFilterable filterable) {

        super(gui, sideIn);

        headerColor = defaultHeaderColor;
        subheaderColor = defaultSubHeaderColor;
        textColor = defaultTextColor;
        backgroundColor = defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 112;
        myFilterable = filterable;

        this.setVisible(filterable::hasFilter);
    }

    @Override
    protected void drawForeground(PoseStack matrixStack) {

        drawPanelIcon(matrixStack, CoreTextures.ICON_FILTER);
        if (!fullyOpen) {
            return;
        }
        fontRenderer().drawShadow(matrixStack, localize("info.cofh.filter"), sideOffset() + 18, 6, headerColor);

        RenderHelper.resetShaderColor();
    }

    @Override
    protected void drawBackground(PoseStack poseStack) {

        super.drawBackground(poseStack);

        // TODO: Render delegate

        if (!fullyOpen) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        RenderHelper.setPosTexShader();
        RenderSystem.setShaderColor(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(poseStack, sideOffset() + slotsBorderX1, slotsBorderY1, 16, 20, slotsBorderX2 - slotsBorderX1, slotsBorderY2 - slotsBorderY1);
        RenderHelper.resetShaderColor();
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (!fullyOpen) {
            tooltipList.add(Component.translatable("info.cofh.filter"));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (!fullyOpen) {
            return false;
        }
        double x = mouseX - this.posX();
        double y = mouseY - this.posY();

        return x >= slotsBorderX1 + sideOffset() && x < slotsBorderX2 + sideOffset() && y >= slotsBorderY1 && y < slotsBorderY2;
    }

}
