package cofh.lib.client.renderer.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.Arrays;

public class BackfaceBakedQuad extends BakedQuad {

    public BackfaceBakedQuad(int[] vertices, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade) {

        super(vertices, tintIndex, direction, sprite, shade);
    }

    public static BackfaceBakedQuad from(BakedQuad quad) {

        return new BackfaceBakedQuad(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), quad.getTintIndex(), FaceBakery.calculateFacing(quad.getVertices()), quad.getSprite(), quad.isShade());
    }

}
