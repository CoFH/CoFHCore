package cofh.core.init;

import cofh.core.client.particle.types.CylindricalParticleType;
import cofh.core.client.particle.types.PointToPointParticleType;
import cofh.core.client.particle.types.RadialParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    //public static List<Consumer<ParticleEngine>> factoryRegistration = new ArrayList<>();

    public static void register() {

        PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));

        PARTICLES.register(ID_PARTICLE_MIST, RadialParticleType::new);
        PARTICLES.register(ID_PARTICLE_BLAST, RadialParticleType::new);

        PARTICLES.register(ID_PARTICLE_SHOCKWAVE, CylindricalParticleType::new);
        PARTICLES.register(ID_PARTICLE_BLAST_WAVE, CylindricalParticleType::new);
        PARTICLES.register(ID_PARTICLE_VORTEX, CylindricalParticleType::new);
        PARTICLES.register(ID_PARTICLE_SPIRAL, CylindricalParticleType::new);

        //PARTICLES.register(ID_PARTICLE_CURRENT, );
        //PARTICLES.register(ID_PARTICLE_BEAM, );
        PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, PointToPointParticleType::new);
        PARTICLES.register(ID_PARTICLE_SHARD, PointToPointParticleType::new);

        //PARTICLES.register(ID_PARTICLE_CIRCLE_ARC, );
        //PARTICLES.register(ID_PARTICLE_FLAME, );
        //PARTICLES.register(ID_PARTICLE_EXPLOSION, );
    }

    //private static void registerSimpleParticle(String id, ParticleEngine.SpriteParticleRegistration<SimpleParticleType> reg) {
    //
    //    registerSimpleParticle(id, false, reg);
    //}
    //
    //private static void registerSimpleParticle(String id, boolean overrideLimit, ParticleEngine.SpriteParticleRegistration<SimpleParticleType> reg) {
    //
    //    registerParticle(id, () -> new SimpleParticleType(overrideLimit), reg);
    //}
    //
    //private static <T extends ParticleOptions, R extends ParticleType<T>> void registerParticle(String id, Supplier<R> supp, ParticleEngine.SpriteParticleRegistration<T> reg) {
    //
    //    RegistryObject<R> type = PARTICLES.register(id, supp);
    //    factoryRegistration.add(engine -> engine.register(type.get(), reg));
    //}

}
