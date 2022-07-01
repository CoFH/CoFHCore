package cofh.core.util.helpers.vfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import static cofh.core.client.CoreRenderType.THICK_LINES;
import static cofh.core.init.CoreShaders.*;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static net.minecraft.client.renderer.RenderStateShard.*;

public class RenderTypes {

    public static final ResourceLocation BLANK_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/blank.png");
    public static final ResourceLocation LIN_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_linear.png");
    public static final ResourceLocation RND_GLOW_TEXTURE = new ResourceLocation(ID_COFH_CORE, "textures/render/glow_round.png");

    public static final RenderType OVERLAY_LINES = RenderType.create("overlay_lines",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, true,
            RenderType.CompositeState.builder().setLineState(THICK_LINES)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setShaderState(RENDERTYPE_LINES_SHADER)
                    .createCompositeState(false));

    //TODO: entity vs particle
    public static final RenderType FLAT_TRANSLUCENT = RenderType.create("flat",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(BLANK_TEXTURE, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(false));

    public static final RenderType LINEAR_GLOW = RenderType.create("glow",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(LIN_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(false));

    public static final RenderType ROUND_GLOW = RenderType.create("glow",
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(RND_GLOW_TEXTURE, true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(MAIN_TARGET)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(COLOR_WRITE)
                    .setShaderState(NEW_ENTITY_SHADER)
                    .createCompositeState(false));

    public static ParticleRenderType CUSTOM = ParticleRenderTypeCoFH.copy(ParticleRenderType.CUSTOM);
    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT_BLEND = new ParticleRenderTypeCoFH() {

        @Override
        public void begin(BufferBuilder builder, TextureManager manager) { //TODO translucent blocks

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(() -> PARTICLE_TRANSLUCENT);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tess) {

            tess.end();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
    };

    public interface ParticleRenderTypeCoFH extends ParticleRenderType {

        static ParticleRenderTypeCoFH copy(ParticleRenderType type) {

            return new ParticleRenderTypeCoFH() {

                @Override
                public void begin(BufferBuilder builder, TextureManager manager) {

                    type.begin(builder, manager);
                }

                @Override
                public void end(Tesselator tess) {

                    type.end(tess);
                }

                @Override
                public String toString() {

                    return type.toString();
                }
            };
        }
    }

}
