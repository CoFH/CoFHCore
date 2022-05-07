package cofh.core.util.helpers.vfx;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static net.minecraft.client.renderer.RenderStateShard.*;

public class RenderTypes {

    public static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/blank.png");
    public static final ResourceLocation LIN_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_linear.png");
    public static final ResourceLocation RND_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_round.png");

    public static final RenderType FLAT_TRANSLUCENT = RenderType.create("flat",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(BLANK_TEXTURE, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(true));

    public static final RenderType LINEAR_GLOW = RenderType.create("glow",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(LIN_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(true));

    public static final RenderType ROUND_GLOW = RenderType.create("glow",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(RND_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(true));

}
