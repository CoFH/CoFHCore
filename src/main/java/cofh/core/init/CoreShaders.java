package cofh.core.init;

import cofh.core.client.PostBuffer;
import cofh.core.common.config.CoreClientConfig;
import cofh.core.util.helpers.RenderHelper;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Consumer;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER;
import static net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreShaders {

    public static ShaderInstance PARTICLE_OVER;
    public static ShaderInstance PARTICLE_ADDITIVE_MULTIPLY;
    public static ShaderInstance PARTICLE_ADDITIVE_SCREEN;
    public static final PostBuffer PIXELATE = new PostBuffer(new ResourceLocation(ID_COFH_CORE, "pixelate")) {

        @Override
        public boolean isEnabled() {

            return super.isEnabled() && RenderHelper.isFabulousGraphics() && CoreClientConfig.stylizedGraphics.get();
        }

        @Override
        public RenderType getRenderType(ResourceLocation texture) {

            RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    //.setCullState(CULL)
                    //.setDepthTestState(NO_DEPTH_TEST)
                    .setOutputState(PIXELATE.getOutputShard());
            if (!isEnabled()) {
                builder.setTransparencyState(TRANSLUCENT_TRANSPARENCY);
            }
            return RenderType.create(outputName, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, buffer.getSize(), false, false, builder.createCompositeState(false));
        }

    };

    @SubscribeEvent
    public static void registerShaders(final RegisterShadersEvent event) throws IOException {

        registerShader(event, "particle_over", DefaultVertexFormat.PARTICLE, s -> PARTICLE_OVER = s);
        registerShader(event, "particle_add", DefaultVertexFormat.PARTICLE, s -> PARTICLE_ADDITIVE_MULTIPLY = s);
        registerShader(event, "particle_screen", DefaultVertexFormat.PARTICLE, s -> PARTICLE_ADDITIVE_SCREEN = s);
    }

    private static void registerShader(RegisterShadersEvent event, String id, VertexFormat format, Consumer<ShaderInstance> callback) throws IOException {

        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(ID_COFH_CORE, id), format), callback);
    }

}
