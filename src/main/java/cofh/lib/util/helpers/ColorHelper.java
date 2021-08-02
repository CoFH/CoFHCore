package cofh.lib.util.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ColorHelper {

    private ColorHelper() {

    }

    public static int getColorFrom(ResourceLocation location) {

        AtlasTexture textureMap = Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = textureMap.getSprite(location);
        return getColorFrom(sprite);
    }

    public static int getColorFrom(TextureAtlasSprite sprite) {

        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = new int[][]{{0}}; // sprite.getFrameTextureData(0);
        int total = 0, red = 0, blue = 0, green = 0;
        for (int pixel : pixelMatrix[pixelMatrix.length - 1]) {
            Color color = new Color(pixel);
            if (color.getAlpha() < 255) continue;
            ++total;
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        if (total > 0) return new Color(red / total, green / total, blue / total, 255).getRGB();
        return -1;
    }

    public static int getColorFrom(TextureAtlasSprite sprite, Color filter) {

        if (sprite == null) return -1;
        if (sprite.getFrameCount() == 0) return -1;
        int[][] pixelMatrix = new int[][]{{0}}; // sprite.getFrameTextureData(0);
        int total = 0, red = 0, blue = 0, green = 0;
        for (int pixel : pixelMatrix[pixelMatrix.length - 1]) {
            Color color = new Color(pixel);
            if (color.getAlpha() < 255 || (color.getRGB() - filter.getRGB() < 100 && color.getRGB() - filter.getRGB() > 100))
                continue;
            ++total;
            red += color.getRed();
            green += color.getGreen();
            blue += color.getBlue();
        }
        if (total > 0) return new Color(red / total, green / total, blue / total, 255).brighter().getRGB();
        return -1;
    }

}