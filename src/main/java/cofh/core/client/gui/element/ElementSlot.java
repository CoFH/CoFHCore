package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static cofh.core.CoFHCore.LOG;
import static cofh.lib.util.Constants.FALSE;
import static cofh.lib.util.Constants.TRUE;

public class ElementSlot extends ElementBase {

    protected ResourceLocation underlayTexture;
    protected ResourceLocation iconTexture;

    protected Supplier<Boolean> drawUnderlay = TRUE;
    protected Supplier<Boolean> drawIcon = TRUE;

    public ElementSlot(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public final ElementSlot setUnderlayTexture(String texture) {

        return setUnderlayTexture(texture, TRUE);
    }

    public final ElementSlot setUnderlayTexture(String texture, Supplier<Boolean> draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL underlay texture.");
            return this;
        }
        this.underlayTexture = new ResourceLocation(texture);
        this.drawUnderlay = draw;
        return this;
    }

    public final ElementSlot setIconTexture(String texture) {

        return setIconTexture(texture, TRUE);
    }

    public final ElementSlot setIconTexture(String texture, Supplier<Boolean> draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL icon texture.");
            return this;
        }
        this.iconTexture = new ResourceLocation(texture);
        this.drawIcon = draw;
        return this;
    }

    public final void clearIconTexture() {

        this.iconTexture = null;
        this.drawIcon = FALSE;
    }

    @Override
    public void drawBackground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        PoseStack poseStack = pGuiGraphics.pose();
        drawSlot(poseStack);
        drawUnderlayTexture(poseStack);
        drawIconTexture(pGuiGraphics.pose());
    }

    protected void drawSlot(PoseStack poseStack) {

        RenderHelper.setPosTexShader();
        RenderHelper.setShaderTexture0(texture);
        drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
    }

    protected void drawUnderlayTexture(PoseStack poseStack) {

        if (drawUnderlay.get() && underlayTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(underlayTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

    protected void drawIconTexture(PoseStack poseStack) {

        if (drawIcon.get() && iconTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(iconTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

}
