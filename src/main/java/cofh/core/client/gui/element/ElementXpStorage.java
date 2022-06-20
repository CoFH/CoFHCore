package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import cofh.core.xp.XpStorage;
import com.mojang.blaze3d.vertex.PoseStack;

import static cofh.lib.util.Constants.FALSE;

public class ElementXpStorage extends ElementResourceStorage {

    public ElementXpStorage(IGuiAccess gui, int posX, int posY, XpStorage storage) {

        super(gui, posX, posY, storage);
        drawStorage = FALSE;
        minDisplay = 0;

        this.claimable = () -> storage.getStored() > 0;
    }

    @Override
    protected void drawResource(PoseStack poseStack) {

        RenderHelper.setPosTexShader();
        RenderHelper.setShaderTexture0(texture);
        int amount = storage.getStored() <= 0 ? 0 : Math.min(getScaled(4) + 1, 4);
        drawTexturedModalRect(poseStack, posX(), posY(), 0, amount * height, width, height);
    }

}
