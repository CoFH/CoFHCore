package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.BooleanSupplier;

import static cofh.core.CoFHCore.LOG;
import static cofh.lib.util.constants.Constants.TRUE;

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
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        drawSlot();
        drawUnderlayTexture();
        drawOverlayTexture();
    }

    protected void drawSlot() {

        RenderHelper.bindTexture(texture);
        drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
    }

    protected void drawUnderlayTexture() {

        if (drawUnderlay.getAsBoolean() && underlayTexture != null) {
            RenderHelper.bindTexture(underlayTexture);
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
    }

    protected void drawOverlayTexture() {

        if (drawOverlay.getAsBoolean() && overlayTexture != null) {
            RenderHelper.bindTexture(overlayTexture);
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
    }

}
