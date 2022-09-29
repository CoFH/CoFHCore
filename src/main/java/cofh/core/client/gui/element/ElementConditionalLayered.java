package cofh.core.client.gui.element;

import cofh.core.client.gui.IGuiAccess;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
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

    public ElementConditionalLayered addSprite(String location, Supplier<Boolean> condition) {

        if (location == null) {
            return this;
        }
        return addSprite(new ResourceLocation(location), condition);
    }

    public ElementConditionalLayered addSprite(ResourceLocation location, Supplier<Boolean> condition) {

        if (!textureExists(location)) {
            return this;
        }
        return addSprite(RenderHelper.getTexture(location), condition);
    }

    public ElementConditionalLayered addSprite(TextureAtlasSprite sprite, Supplier<Boolean> condition) {

        return addSprite(() -> sprite, condition);
    }

    public ElementConditionalLayered addSprite(Supplier<TextureAtlasSprite> sprite, Supplier<Boolean> condition) {

        return addSprite(sprite, WHITE, condition);
    }

    public ElementConditionalLayered addSprite(Supplier<TextureAtlasSprite> sprite, IntSupplier color, Supplier<Boolean> condition) {

        conditionalTextures.add(new IconWrapper(sprite, color, condition));
        return this;
    }

    @Override
    public void drawBackground(PoseStack matrixStack, int mouseX, int mouseY) {

        for (IconWrapper icon : conditionalTextures) {
            if (icon.display.get()) {
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
        protected Supplier<Boolean> display;

        IconWrapper(Supplier<TextureAtlasSprite> texture, IntSupplier color, Supplier<Boolean> display) {

            this.texture = texture;
            this.color = color;
            this.display = display;
        }

    }
    // endregion
}
