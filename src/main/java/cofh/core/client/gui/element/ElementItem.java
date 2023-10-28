package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.EMPTY_ITEM;

public class ElementItem extends ElementBase {

    protected Supplier<ItemStack> renderStack = EMPTY_ITEM;

    public ElementItem(IGuiAccess gui) {

        super(gui);
    }

    public ElementItem(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementItem setItem(Supplier<ItemStack> supplier) {

        this.renderStack = supplier;
        return this;
    }

    @Override
    public void drawForeground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        if (!renderStack.get().isEmpty()) {
            pGuiGraphics.renderItem(renderStack.get(), posX(), posY());
        }
    }

}
