package cofh.core.util.helpers.vfx;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static net.minecraft.client.renderer.RenderState.*;

public class RenderTypes {

    public static final ResourceLocation LIN_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_linear.png");
    public static final ResourceLocation RND_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_round.png");

    public static final RenderType BLANK_TRANSLUCENT = RenderType.create("blank",
            DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
            RenderType.State.builder().setTextureState(NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    public static final RenderType LINEAR_GLOW = RenderType.create("glow",
            DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
            RenderType.State.builder().setTextureState(new TextureState(LIN_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

    public static final RenderType ROUND_GLOW = RenderType.create("glow",
            DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true,
            RenderType.State.builder().setTextureState(new TextureState(RND_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(true));

}
