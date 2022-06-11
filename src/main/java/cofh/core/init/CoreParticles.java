package cofh.core.init;

import cofh.core.client.particle.*;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    public static List<Consumer<ParticleEngine>> factoryRegistration = new ArrayList<>();

    public static void register() {

        registerSimpleParticle(ID_PARTICLE_SHOCKWAVE, false, ShockwaveParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_FROST, false, FrostParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_ICE_MIST, false, MistParticle::iceMist);
        registerSimpleParticle(ID_PARTICLE_SPARK, false, SparkParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_PLASMA, false, PlasmaBallParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_STRAIGHT_ARC, false, ArcParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_BLAST_WAVE, false, BlastWaveParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_VORTEX, false, WindVortexParticle.Factory::new);
        registerSimpleParticle(ID_PARTICLE_SPIRAL, false, WindSpiralParticle.Factory::new);
        //registerSimpleParticle(ID_PARTICLE_CURRENT, false, );
        //registerSimpleParticle(ID_PARTICLE_CIRCLE_ARC, false, );
        //registerSimpleParticle(ID_PARTICLE_FLAME, false, );
        //registerSimpleParticle(ID_PARTICLE_BLAST, false, );
        //registerSimpleParticle(ID_PARTICLE_BEAM, false, );
        //registerSimpleParticle(ID_PARTICLE_BULLET, false, );
    }

    private static void registerSimpleParticle(String id, boolean overrideLimit, ParticleEngine.SpriteParticleRegistration<SimpleParticleType> reg) {

        RegistryObject<SimpleParticleType> type = PARTICLES.register(id, () -> new SimpleParticleType(overrideLimit));
        factoryRegistration.add(engine -> engine.register(type.get(), reg));
    }

}
