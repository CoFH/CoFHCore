package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.BUCKET_VOLUME;
import static cofh.lib.util.constants.Constants.EMPTY_BLOCK;

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
    public void drawForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        Block block = blockSup.get();
        if (block != Blocks.AIR) {
            if (block != renderBlock) {
                if (block instanceof FlowingFluidBlock) {
                    renderFluid = new FluidStack(((FlowingFluidBlock) block).getFluid(), BUCKET_VOLUME);
                    renderStack = ItemStack.EMPTY;
                } else {
                    renderFluid = FluidStack.EMPTY;
                    renderStack = new ItemStack(block);
                }
                renderBlock = block;
            }
            // GL11.glPushMatrix();
            if (!renderStack.isEmpty()) {
                RenderHelper.renderItem().renderItemAndEffectIntoGUI(renderStack, posX(), posY());
            } else {
                RenderHelper.drawFluid(posX(), posY(), renderFluid, 16, 16);
            }
            // GL11.glPopMatrix();
        }
    }

}
