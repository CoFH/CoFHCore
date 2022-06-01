package cofh.core.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreShaders {

    public static ShaderInstance PARTICLE_TRANSLUCENT;

    @SubscribeEvent
    public static void registerShaders(final RegisterShadersEvent event) throws IOException {

        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(ID_COFH_CORE, "particle"), DefaultVertexFormat.PARTICLE), s -> CoreShaders.PARTICLE_TRANSLUCENT = s);
    }
}
