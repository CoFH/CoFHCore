package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.content.fluid.FluidStorageCoFH;
import cofh.core.util.helpers.FluidHelper;
import cofh.core.util.helpers.RenderHelper;
import cofh.lib.util.helpers.StringHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ElementFluidStorage extends ElementResourceStorage {

    protected FluidStorageCoFH tank;
    protected TextureAtlasSprite fluidTexture;

    public ElementFluidStorage(IGuiAccess gui, int posX, int posY, FluidStorageCoFH storage) {

        super(gui, posX, posY, storage);
        this.tank = storage;
    }

    public ElementFluidStorage setFluidTexture(TextureAtlasSprite fluidTexture) {

        this.fluidTexture = fluidTexture;
        return this;
    }

    @Override
    protected void drawResource(PoseStack poseStack) {

        int resourceHeight = height - 2;
        int resourceWidth = width - 2;

        int amount = getScaled(resourceHeight);
        boolean invert = FluidHelper.density(tank.getFluidStack()) < 0;

        if (fluidTexture != null) {
            RenderHelper.setBlockTextureSheet();
            RenderHelper.drawTiledTexture(guiLeft() + posX() + 1, guiTop() + posY() + 1 + (invert ? 0 : resourceHeight - amount), fluidTexture, resourceWidth, amount);
        } else {
            RenderHelper.drawFluid(guiLeft() + posX() + 1, guiTop() + posY() + 1 + (invert ? 0 : resourceHeight - amount), tank.getFluidStack(), resourceWidth, amount);
        }
    }

    @Override
    protected void drawOverlayTexture(PoseStack poseStack) {

        if (!drawOverlay.getAsBoolean()) {
            return;
        }
        if (storage.isCreative() && creativeTexture != null) {
            RenderHelper.setShaderTexture0(creativeTexture);
            drawTexturedModalRect(poseStack, posX(), posY(), 0, 0, width, height);
        } else {
            super.drawOverlayTexture(poseStack);
        }
    }

    @Override
    public void addTooltip(List<Component> tooltipList, int mouseX, int mouseY) {

        if (storage.getStored() > 0) {
            tooltipList.add(StringHelper.getFluidName(tank.getFluidStack()));
            if (FluidHelper.hasPotionTag(tank.getFluidStack())) {
                FluidHelper.addPotionTooltip(tank.getFluidStack(), tooltipList);
            }
        }
        super.addTooltip(tooltipList, mouseX, mouseY);
    }

}
