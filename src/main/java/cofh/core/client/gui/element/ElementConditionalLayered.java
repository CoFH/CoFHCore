package cofh.core.client.gui.element;

import cofh.core.util.helpers.RenderHelper;
import cofh.lib.client.gui.IGuiAccess;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static cofh.core.util.helpers.RenderHelper.textureExists;

public class ElementConditionalLayered extends ElementBase {

    protected static final IntSupplier WHITE = () -> 0xFFFFFF;

    protected ArrayList<IconWrapper> conditionalTextures = new ArrayList<>();

    public ElementConditionalLayered(IGuiAccess gui) {

        super(gui);
    }

    public ElementConditionalLayered(IGuiAccess gui, int posX, int posY) {

        super(gui, posX, posY);
    }

    public ElementConditionalLayered addSprite(String location, BooleanSupplier condition) {

        if (location == null) {
            return this;
        }
        return addSprite(new ResourceLocation(location), condition);
    }

    public ElementConditionalLayered addSprite(ResourceLocation location, BooleanSupplier condition) {

        if (!textureExists(location)) {
            return this;
        }
        return addSprite(RenderHelper.getTexture(location), condition);
    }

    public ElementConditionalLayered addSprite(TextureAtlasSprite sprite, BooleanSupplier condition) {

        return addSprite(() -> sprite, condition);
    }

    public ElementConditionalLayered addSprite(Supplier<TextureAtlasSprite> sprite, BooleanSupplier condition) {

        return addSprite(sprite, WHITE, condition);
    }

    public ElementConditionalLayered addSprite(Supplier<TextureAtlasSprite> sprite, IntSupplier color, BooleanSupplier condition) {

        conditionalTextures.add(new IconWrapper(sprite, color, condition));
        return this;
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        for (IconWrapper icon : conditionalTextures) {
            if (icon.display.getAsBoolean()) {
                if (icon.color != WHITE) {
                    gui.drawIcon(matrixStack, icon.texture.get(), icon.color.getAsInt(), posX(), posY());
                } else {
                    gui.drawIcon(matrixStack, icon.texture.get(), posX(), posY());
                }
            }
        }
    }

    // region ICON WRAPPER
    protected static class IconWrapper {

        protected Supplier<TextureAtlasSprite> texture;
        protected IntSupplier color;
        protected BooleanSupplier display;

        IconWrapper(Supplier<TextureAtlasSprite> texture, IntSupplier color, BooleanSupplier display) {

            this.texture = texture;
            this.color = color;
            this.display = display;
        }

    }
    // endregion
}
