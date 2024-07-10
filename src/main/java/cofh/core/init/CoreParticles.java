package cofh.core.init;

import cofh.core.client.particle.types.ColorParticleType;
import cofh.core.client.particle.types.CylindricalParticleType;
import cofh.core.client.particle.types.PointToPointParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.core.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    //public static List<Consumer<ParticleEngine>> factoryRegistration = new ArrayList<>();

    public static void register() {

    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FROST = PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK = PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PLASMA = PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(true));

    public static final DeferredHolder<ParticleType<?>, ColorParticleType> FIRE = PARTICLES.register(ID_PARTICLE_FIRE, () -> new ColorParticleType());
    public static final DeferredHolder<ParticleType<?>, ColorParticleType> BLAST = PARTICLES.register(ID_PARTICLE_BLAST, () -> new ColorParticleType());
    public static final DeferredHolder<ParticleType<?>, ColorParticleType> MIST = PARTICLES.register(ID_PARTICLE_MIST, () -> new ColorParticleType());

    public static final DeferredHolder<ParticleType<?>, CylindricalParticleType> BLAST_WAVE = PARTICLES.register(ID_PARTICLE_BLAST_WAVE, () -> new CylindricalParticleType());
    public static final DeferredHolder<ParticleType<?>, CylindricalParticleType> SHOCKWAVE = PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new CylindricalParticleType());
    public static final DeferredHolder<ParticleType<?>, CylindricalParticleType> WIND_SPIRAL = PARTICLES.register(ID_PARTICLE_WIND_SPIRAL, () -> new CylindricalParticleType());
    public static final DeferredHolder<ParticleType<?>, CylindricalParticleType> WIND_VORTEX = PARTICLES.register(ID_PARTICLE_WIND_VORTEX, () -> new CylindricalParticleType());

    public static final DeferredHolder<ParticleType<?>, PointToPointParticleType> BEAM = PARTICLES.register(ID_PARTICLE_BEAM, () -> new PointToPointParticleType());
    public static final DeferredHolder<ParticleType<?>, PointToPointParticleType> SHARD = PARTICLES.register(ID_PARTICLE_SHARD, () -> new PointToPointParticleType());
    public static final DeferredHolder<ParticleType<?>, PointToPointParticleType> STRAIGHT_ARC = PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, () -> new PointToPointParticleType());
    public static final DeferredHolder<ParticleType<?>, PointToPointParticleType> STREAM = PARTICLES.register(ID_PARTICLE_STREAM, () -> new PointToPointParticleType());

}
