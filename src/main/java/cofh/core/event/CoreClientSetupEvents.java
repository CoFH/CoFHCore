package cofh.core.event;

import cofh.core.client.model.FluidContainerItemModel;
import cofh.core.client.particle.impl.*;
import cofh.lib.api.item.IColorableItem;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
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

        event.register(FROST.get(), FrostParticle.Factory::new);
        event.register(PLASMA.get(), PlasmaBallParticle.Factory::new);
        event.register(SPARK.get(), SparkParticle.Factory::new);

        event.register(FIRE.get(), FireParticle::factory);
        event.register(BLAST.get(), BlastParticle::factory);
        event.register(MIST.get(), MistParticle::factory);

        event.register(SHOCKWAVE.get(), ShockwaveParticle::factory);
        event.register(BLAST_WAVE.get(), BlastWaveParticle::factory);
        event.register(WIND_VORTEX.get(), WindVortexParticle::factory);
        event.register(WIND_SPIRAL.get(), WindSpiralParticle::factory);

        event.register(BEAM.get(), BeamParticle::factory);
        event.register(STRAIGHT_ARC.get(), ArcParticle::factory);
        event.register(SHARD.get(), ShardParticle::factory);
        event.register(STREAM.get(), StreamParticle::factory);
    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}
