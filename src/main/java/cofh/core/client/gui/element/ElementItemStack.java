package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.EMPTY_ITEM;

public class ElementItemStack extends ElementBase {

    protected Supplier<ItemStack> renderStack = EMPTY_ITEM;

    public ElementItemStack(IGuiAccess gui) {

        super(gui);
    }

    public ElementItemStack(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementItemStack setItem(Supplier<ItemStack> supplier) {

        this.renderStack = supplier;
        return this;
    }

    @Override
    public void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        if (!renderStack.get().isEmpty()) {
            // GL11.glPushMatrix();
            RenderHelper.renderItem().renderItemAndEffectIntoGUI(renderStack.get().getStack(), posX(), posY());
            // GL11.glPopMatrix();
        }
    }

}
