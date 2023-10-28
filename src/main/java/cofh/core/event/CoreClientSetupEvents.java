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

        event.registerSpriteSet(FROST.get(), FrostParticle::factory);
        event.registerSpriteSet(PLASMA.get(), PlasmaBallParticle::factory);
        event.registerSpriteSet(SPARK.get(), SparkParticle::factory);

        event.registerSpriteSet(FIRE.get(), FireParticle::factory);
        event.registerSpriteSet(BLAST.get(), BlastParticle::factory);
        event.registerSpriteSet(MIST.get(), MistParticle::factory);

        event.registerSpriteSet(SHOCKWAVE.get(), ShockwaveParticle::factory);
        event.registerSpriteSet(BLAST_WAVE.get(), BlastWaveParticle::factory);
        event.registerSpriteSet(WIND_VORTEX.get(), WindVortexParticle::factory);
        event.registerSpriteSet(WIND_SPIRAL.get(), WindSpiralParticle::factory);

        event.registerSpriteSet(BEAM.get(), BeamParticle::factory);
        event.registerSpriteSet(STRAIGHT_ARC.get(), ArcParticle::factory);
        event.registerSpriteSet(SHARD.get(), ShardParticle::factory);
        event.registerSpriteSet(STREAM.get(), StreamParticle::factory);
    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}
