package cofh.core.client.event;

import cofh.core.client.PostEffect;
import cofh.core.client.model.FluidContainerItemModel;
import cofh.core.client.particle.impl.*;
import cofh.lib.api.item.IColorableItem;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.client.CoreKeys.MULTIMODE_DECREMENT;
import static cofh.core.client.CoreKeys.MULTIMODE_INCREMENT;
import static cofh.core.init.CoreParticles.*;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;

@Mod.EventBusSubscriber (value = Dist.CLIENT, modid = ID_COFH_CORE, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreClientSetupEvents {

    private static final List<Item> COLORABLE_ITEMS = new ArrayList<>();

    private CoreClientSetupEvents() {

    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {

        event.register(MULTIMODE_INCREMENT);
        event.register(MULTIMODE_DECREMENT);
    }

    @SubscribeEvent
    public static void colorSetupItem(final RegisterColorHandlersEvent.Item event) {

        ItemColors colors = event.getItemColors();
        for (Item colorable : COLORABLE_ITEMS) {
            colors.register(((IColorableItem) colorable)::getColor, colorable);
        }
    }

    @SubscribeEvent
    public static void registerModels(final RegisterGeometryLoaders event) {

        event.register("fluid_container", new FluidContainerItemModel.Loader());
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {

        event.registerSpriteSet(FROST.get(), FrostParticle::factory);
        event.registerSpriteSet(PLASMA.get(), PlasmaBallParticle::factory);
        event.registerSpriteSet(SPARK.get(), SparkParticle::factory);

        event.registerSpriteSet(FIRE.get(), FireParticle::factory);
        event.registerSpriteSet(BLAST.get(), BlastParticle::factory);
        event.registerSpecial(PULSE.get(), PulseParticle::new);
        event.registerSpriteSet(MIST.get(), MistParticle::factory);
        event.registerSpriteSet(SQUARE.get(), SquareParticle::factory);

        event.registerSpecial(SHOCKWAVE.get(), ShockwaveParticle::new);
        event.registerSpecial(BLAST_WAVE.get(), BlastWaveParticle::new);
        event.registerSpecial(WIND_VORTEX.get(), WindVortexParticle::new);
        event.registerSpecial(WIND_SPIRAL.get(), WindSpiralParticle::new);
        event.registerSpecial(RING.get(), RingParticle::new);

        event.registerSpecial(BEAM.get(), BeamParticle::new);
        event.registerSpecial(STRAIGHT_ARC.get(), ArcParticle::new);
        event.registerSpecial(SHARD.get(), ShardParticle::new);
        event.registerSpecial(STREAM.get(), StreamParticle::new);
    }

    @SubscribeEvent
    public static void registerReloadListeners(final RegisterClientReloadListenersEvent event) {

        for (PostEffect effect : PostEffect.getAllEffects()) {
            event.registerReloadListener(effect);
        }
    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}
