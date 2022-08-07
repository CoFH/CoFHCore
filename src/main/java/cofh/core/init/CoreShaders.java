package cofh.core.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Consumer;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreShaders {

    public static ShaderInstance PARTICLE_OVER;
    public static ShaderInstance PARTICLE_ADDITIVE_MULTIPLY;
    public static ShaderInstance PARTICLE_ADDITIVE_SCREEN;
    public static ShaderInstance ENTITY_PIXELATE;

    @SubscribeEvent
    public static void registerShaders(final RegisterShadersEvent event) throws IOException {

        registerShader(event, "particle_over", DefaultVertexFormat.PARTICLE, s -> PARTICLE_OVER = s);
        registerShader(event, "particle_add", DefaultVertexFormat.PARTICLE, s -> PARTICLE_ADDITIVE_MULTIPLY = s);
        registerShader(event, "particle_screen", DefaultVertexFormat.PARTICLE, s -> PARTICLE_ADDITIVE_SCREEN = s);
        registerShader(event, "pixelation", DefaultVertexFormat.NEW_ENTITY, s -> ENTITY_PIXELATE = s);
    }

    private static void registerShader(RegisterShadersEvent event, String id, VertexFormat format, Consumer<ShaderInstance> callback) throws IOException {

        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ID_COFH_CORE, id), format), callback);
    }

}
