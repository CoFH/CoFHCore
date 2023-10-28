package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;

import static cofh.lib.util.Constants.BUCKET_VOLUME;
import static cofh.lib.util.Constants.EMPTY_BLOCK;

public class ElementBlock extends ElementBase {

    protected Supplier<Block> blockSup = EMPTY_BLOCK;

    protected Block renderBlock = Blocks.AIR;
    protected ItemStack renderStack = ItemStack.EMPTY;
    protected FluidStack renderFluid = FluidStack.EMPTY;

    public ElementBlock(IGuiAccess gui) {

        super(gui);
    }

    public ElementBlock(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementBlock setBlock(Supplier<Block> supplier) {

        this.blockSup = supplier;
        return this;
    }

    @Override
    public void drawForeground(GuiGraphics pGuiGraphics, int mouseX, int mouseY) {

        Block block = blockSup.get();
        if (block != Blocks.AIR) {
            if (block != renderBlock) {
                if (block instanceof LiquidBlock) {
                    renderFluid = new FluidStack(((LiquidBlock) block).getFluid(), BUCKET_VOLUME);
                    renderStack = ItemStack.EMPTY;
                } else {
                    renderFluid = FluidStack.EMPTY;
                    renderStack = new ItemStack(block);
                }
                renderBlock = block;
            }
            if (!renderStack.isEmpty()) {
                pGuiGraphics.renderItem(renderStack, posX(), posY());
            } else {
                RenderHelper.drawFluid(posX(), posY(), renderFluid, 16, 16);
            }
        }
    }

}
