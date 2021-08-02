package cofh.lib.client.renderer.model;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;

/**
 * Revived from 1.14
 *
 * @author Mojang
 * Thanks tterrag!
 */
@OnlyIn(Dist.CLIENT)
public class RetexturedBakedQuad extends BakedQuad {

    private final TextureAtlasSprite texture;

    public RetexturedBakedQuad(BakedQuad quad, TextureAtlasSprite textureIn) {

        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.applyDiffuseLighting());
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad() {

        for (int i = 0; i < 4; ++i) {
            int j = DefaultVertexFormats.BLOCK.getIntegerSize() * i;
            int uvIndex = 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU(getUnInterpolatedU(this.sprite, Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(getUnInterpolatedV(this.sprite, Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {

        return texture;
    }

    private static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {

        float f = sprite.getMaxU() - sprite.getMinU();
        return (u - sprite.getMinU()) / f * 16.0F;
    }

    private static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {

        float f = sprite.getMaxV() - sprite.getMinV();
        return (v - sprite.getMinV()) / f * 16.0F;
    }

}