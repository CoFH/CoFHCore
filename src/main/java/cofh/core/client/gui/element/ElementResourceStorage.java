package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import cofh.lib.util.IResourceStorage;
import cofh.lib.util.helpers.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BooleanSupplier;

import static cofh.core.CoFHCore.LOG;
import static cofh.lib.util.constants.Constants.FALSE;
import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.helpers.StringHelper.format;
import static net.minecraft.client.gui.screens.Screen.hasAltDown;
import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

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
    public void drawBackground(PoseStack poseStack, int mouseX, int mouseY) {

        drawStorage(poseStack);
        drawUnderlayTexture(poseStack);
        drawResource(poseStack);
        drawOverlayTexture(poseStack);
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (storage.isCreative()) {
            tooltipList.add(new TranslatableComponent("info.cofh.infinite").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC));
        } else {
            tooltipList.add(new TextComponent(format(storage.getStored()) + " / " + format(storage.getCapacity()) + " " + storage.getUnit()));
        }
        if (clearable.getAsBoolean() && clearStorage != FALSE && (hasAltDown() || hasShiftDown())) {
            tooltipList.add(new TranslatableComponent("info.cofh.click_to_clear").withStyle(ChatFormatting.GRAY));
        } else if (claimable.getAsBoolean()) {
            tooltipList.add(new TranslatableComponent("info.cofh.click_to_claim").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        if (clearable.getAsBoolean() && hasShiftDown() && hasAltDown()) {
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

    protected void drawStorage(PoseStack poseStack) {

        if (drawStorage.getAsBoolean() && texture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(texture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

    protected void drawUnderlayTexture(PoseStack poseStack) {

        if (drawUnderlay.getAsBoolean() && underlayTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(underlayTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

    protected abstract void drawResource(PoseStack poseStack);

    protected void drawOverlayTexture(PoseStack poseStack) {

        if (drawOverlay.getAsBoolean() && overlayTexture != null) {
            RenderHelper.setPosTexShader();
            RenderHelper.setShaderTexture0(overlayTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        }
    }

}
