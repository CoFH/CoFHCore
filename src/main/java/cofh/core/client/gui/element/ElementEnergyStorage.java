package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.energy.EnergyStorageCoFH;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;

public class ElementEnergyStorage extends ElementResourceStorage {

    public ElementEnergyStorage(IGuiAccess gui, int posX, int posY, EnergyStorageCoFH storage) {

        super(gui, posX, posY, storage);
        clearable = () -> !storage.isCreative();
    }

    @Override
    protected void drawResource(PoseStack poseStack) {

        if (storage.isCreative() && creativeTexture != null) {
            RenderHelper.setShaderTexture0(creativeTexture);
        } else {
            RenderHelper.setShaderTexture0(texture);
        }
        int resourceHeight = height - 2;
        int amount = getScaled(resourceHeight);
        drawTexturedModalRect(poseStack, posX(), posY() + 1 + resourceHeight - amount, width, 1 + resourceHeight - amount, width, amount);
    }

}
