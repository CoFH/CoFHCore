package cofh.core.util.helpers.vfx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static cofh.core.client.CoreRenderType.THICK_LINES;
import static cofh.core.init.CoreShaders.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
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

    public static final RenderType FLAT_CUTOUT = opaque("cofh_opaque", new TextureStateShard(BLANK_TEXTURE, false, false));
    public static final RenderType FLAT_TRANSLUCENT = translucentNoDepthWrite(BLANK_TEXTURE);
    public static final RenderType LINEAR_GLOW = translucentNoDepthWrite(LIN_GLOW_TEXTURE);
    public static final RenderType ROUND_GLOW = translucentNoDepthWrite(RND_GLOW_TEXTURE);

    public static RenderType opaque(String name, TextureStateShard texture) {

        return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setTextureState(texture)
                        .setShaderState(NEW_ENTITY_SHADER)
                        .createCompositeState(false));
    }

    public static RenderType translucent(ResourceLocation texture) {

        return RenderType.create("cofh_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setShaderState(NEW_ENTITY_SHADER)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));
    }

    public static RenderType translucentNoCull(ResourceLocation texture) {

        return RenderType.create("cofh_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setShaderState(NEW_ENTITY_SHADER)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));
    }

    public static RenderType translucentNoDepthWrite(ResourceLocation texture) {

        return RenderType.create("cofh_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setShaderState(NEW_ENTITY_SHADER)
                        .setWriteMaskState(COLOR_WRITE)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false));
    }

    public static ParticleRenderType PARTICLE_SHEET_OVER = translucentSheet(() -> PARTICLE_OVER);
    public static ParticleRenderType PARTICLE_SHEET_ADDITIVE_MULTIPLY = translucentSheet(() -> PARTICLE_ADDITIVE_MULTIPLY);
    public static ParticleRenderType PARTICLE_SHEET_ADDITIVE_SCREEN = translucentSheet(() -> PARTICLE_ADDITIVE_SCREEN);

    static ParticleRenderType translucentSheet(Supplier<ShaderInstance> shader) {

        return new ParticleRenderType() {

            @Override
            public void begin(BufferBuilder builder, TextureManager manager) {

                RenderSystem.depthMask(false); //TODO post shader
                RenderSystem.enableBlend();
                RenderSystem.setShader(shader);
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
    }

}
