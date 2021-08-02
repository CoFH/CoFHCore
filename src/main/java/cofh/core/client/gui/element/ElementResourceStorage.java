package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.util.IResourceStorage;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.core.CoFHCore.LOG;
import static cofh.lib.util.constants.Constants.FALSE;
import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.format;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public abstract class ElementResourceStorage extends ElementBase {

    protected ResourceLocation creativeTexture;
    protected ResourceLocation underlayTexture;
    protected ResourceLocation overlayTexture;

    protected IResourceStorage storage;
    protected int minDisplay = 1;

    protected BooleanSupplier drawStorage = TRUE;
    protected BooleanSupplier drawUnderlay = TRUE;
    protected BooleanSupplier drawOverlay = TRUE;

    protected BooleanSupplier claimStorage = FALSE;
    protected BooleanSupplier clearStorage = FALSE;
    protected BooleanSupplier claimable = FALSE;
    protected BooleanSupplier clearable;

    public ElementResourceStorage(IGuiAccess gui, int posX, int posY, IResourceStorage storage) {

        super(gui, posX, posY);
        this.storage = storage;

        this.clearable = () -> storage.getStored() > 0;
    }

    public final ElementResourceStorage setCreativeTexture(String texture) {

        if (texture == null) {
            LOG.warn("Attempted to assign a NULL creative texture.");
            return this;
        }
        this.creativeTexture = new ResourceLocation(texture);
        return this;
    }

    public final ElementResourceStorage setUnderlayTexture(String texture) {

        return setUnderlayTexture(texture, TRUE);
    }

    public final ElementResourceStorage setUnderlayTexture(String texture, BooleanSupplier draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL underlay texture.");
            return this;
        }
        this.underlayTexture = new ResourceLocation(texture);
        this.drawUnderlay = draw;
        return this;
    }

    public final ElementResourceStorage setOverlayTexture(String texture) {

        return setOverlayTexture(texture, TRUE);
    }

    public final ElementResourceStorage setOverlayTexture(String texture, BooleanSupplier draw) {

        if (texture == null || draw == null) {
            LOG.warn("Attempted to assign a NULL overlay texture.");
            return this;
        }
        this.overlayTexture = new ResourceLocation(texture);
        this.drawOverlay = draw;
        return this;
    }

    public final ElementResourceStorage setClaimStorage(BooleanSupplier claimStorage) {

        this.claimStorage = claimStorage;
        return this;
    }

    public final ElementResourceStorage setClearStorage(BooleanSupplier clearStorage) {

        this.clearStorage = clearStorage;
        return this;
    }

    public ElementResourceStorage setMinDisplay(int minDisplay) {

        this.minDisplay = minDisplay;
        return this;
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        drawStorage();
        drawUnderlayTexture();
        drawResource();
        drawOverlayTexture();
    }

    @Override
    public void addTooltip(List<ITextComponent> tooltipList, int mouseX, int mouseY) {

        if (storage.isCreative()) {
            tooltipList.add(new TranslationTextComponent("info.cofh.infinite").mergeStyle(TextFormatting.LIGHT_PURPLE).mergeStyle(TextFormatting.ITALIC));
        } else {
            tooltipList.add(new StringTextComponent(format(storage.getStored()) + " / " + format(storage.getCapacity()) + " " + storage.getUnit()));
        }
        if (clearable.getAsBoolean() && clearStorage != FALSE && (hasAltDown() || hasShiftDown())) {
            tooltipList.add(new TranslationTextComponent("info.cofh.click_to_clear").mergeStyle(TextFormatting.GRAY));
        } else if (claimable.getAsBoolean()) {
            tooltipList.add(new TranslationTextComponent("info.cofh.click_to_claim").mergeStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (clearable.getAsBoolean() && hasShiftDown() && Screen.hasAltDown()) {
            return clearStorage.getAsBoolean();
        }
        if (claimable.getAsBoolean()) {
            return claimStorage.getAsBoolean();
        }
        return false;
    }

    protected int getScaled(int scale) {

        double fraction = (double) storage.getStored() * scale / storage.getCapacity();
        int amount = MathHelper.clamp(MathHelper.round(fraction), 0, scale);
        return fraction > 0 ? Math.max(minDisplay, amount) : amount;
    }

    protected void drawStorage() {

        if (drawStorage.getAsBoolean() && texture != null) {
            RenderHelper.bindTexture(texture);
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
    }

    protected void drawUnderlayTexture() {

        if (drawUnderlay.getAsBoolean() && underlayTexture != null) {
            RenderHelper.bindTexture(underlayTexture);
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
    }

    protected abstract void drawResource();

    protected void drawOverlayTexture() {

        if (drawOverlay.getAsBoolean() && overlayTexture != null) {
            RenderHelper.bindTexture(overlayTexture);
            drawTexturedModalRect(posX(), posY(), 0, 0, width, height);
        }
    }

}
