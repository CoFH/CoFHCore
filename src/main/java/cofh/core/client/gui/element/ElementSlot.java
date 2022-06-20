package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BooleanSupplier;

import static cofh.core.CoFHCore.LOG;
import static cofh.lib.util.Constants.TRUE;

public class ElementSlot extends ElementBase {

    protected ResourceLocation underlayTexture;
    protected ResourceLocation overlayTexture;

    protected BooleanSupplier drawUnderlay = TRUE;
    protected BooleanSupplier drawOverlay = TRUE;

    public ElementSlot(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public final ElementSlot setUnderlayTexture(String texture) {

        return setUnderlayTexture(texture, TRUE);
    }

    public final ElementSlot setUnderlayTexture(String texture, BooleanSupplier draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL underlay texture.");
            return this;
        }
        this.underlayTexture = new ResourceLocation(texture);
        this.drawUnderlay = draw;
        return this;
    }

    public final ElementSlot setOverlayTexture(String texture) {

        return setOverlayTexture(texture, TRUE);
    }

    public final ElementSlot setOverlayTexture(String texture, BooleanSupplier draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL overlay texture.");
            return this;
        }
        this.overlayTexture = new ResourceLocation(texture);
        this.drawOverlay = draw;
        return this;
    }

    @Override
    public void drawBackground(PoseStack poseStack, int mouseX, int mouseY) {

        drawSlot(poseStack);
        drawUnderlayTexture(poseStack);

    }

    @Override
    public void drawForeground(PoseStack poseStack, int mouseX, int mouseY) {

        drawOverlayTexture(poseStack);
    }

    protected void drawSlot(PoseStack poseStack) {

        RenderHelper.setPosTexShader();
        RenderHelper.setShaderTexture0(texture);
        drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
    }

    protected void drawUnderlayTexture(PoseStack poseStack) {

        if (drawUnderlay.getAsBoolean() && underlayTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(underlayTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

    protected void drawOverlayTexture(PoseStack poseStack) {

        if (drawOverlay.getAsBoolean() && overlayTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(overlayTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

}
