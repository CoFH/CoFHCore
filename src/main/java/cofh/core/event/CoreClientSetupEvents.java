package cofh.core.event;

import cofh.core.client.particle.impl.*;
import cofh.lib.api.item.IColorableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.particle.ParticleEngine;
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

    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {

        ParticleEngine manager = Minecraft.getInstance().particleEngine;

        manager.register(FROST.get(), FrostParticle.Factory::new);
        manager.register(PLASMA.get(), PlasmaBallParticle.Factory::new);
        manager.register(SPARK.get(), SparkParticle.Factory::new);

        manager.register(FIRE.get(), FireParticle::factory);
        manager.register(BLAST.get(), BlastParticle::factory);
        manager.register(MIST.get(), MistParticle::factory);

        manager.register(SHOCKWAVE.get(), ShockwaveParticle::factory);
        manager.register(BLAST_WAVE.get(), BlastWaveParticle::factory);
        manager.register(WIND_VORTEX.get(), WindVortexParticle::factory);
        manager.register(WIND_SPIRAL.get(), WindSpiralParticle::factory);

        manager.register(BEAM.get(), BeamParticle::factory);
        manager.register(STRAIGHT_ARC.get(), ArcParticle::factory);
        manager.register(SHARD.get(), ShardParticle::factory);
    }

    // region HELPERS
    public static void addColorable(Item colorable) {

        if (colorable instanceof IColorableItem) {
            COLORABLE_ITEMS.add(colorable);
        }
    }
    // endregion
}
