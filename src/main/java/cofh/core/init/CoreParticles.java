package cofh.core.init;

import cofh.core.client.particle.impl.StreamParticle;
import cofh.core.client.particle.types.ColorParticleType;
import cofh.core.client.particle.types.CylindricalParticleType;
import cofh.core.client.particle.types.PointToPointParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.core.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    //public static List<Consumer<ParticleEngine>> factoryRegistration = new ArrayList<>();

    public static void register() {

    }

    public static final RegistryObject<SimpleParticleType> FROST = PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SPARK = PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> PLASMA = PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(true));

    public static final RegistryObject<ColorParticleType> FIRE = PARTICLES.register(ID_PARTICLE_FIRE, ColorParticleType::new);
    public static final RegistryObject<ColorParticleType> BLAST = PARTICLES.register(ID_PARTICLE_BLAST, ColorParticleType::new);
    public static final RegistryObject<ColorParticleType> MIST = PARTICLES.register(ID_PARTICLE_MIST, ColorParticleType::new);

    public static final RegistryObject<CylindricalParticleType> BLAST_WAVE = PARTICLES.register(ID_PARTICLE_BLAST_WAVE, CylindricalParticleType::new);
    public static final RegistryObject<CylindricalParticleType> SHOCKWAVE = PARTICLES.register(ID_PARTICLE_SHOCKWAVE, CylindricalParticleType::new);
    public static final RegistryObject<CylindricalParticleType> WIND_SPIRAL = PARTICLES.register(ID_PARTICLE_WIND_SPIRAL, CylindricalParticleType::new);
    public static final RegistryObject<CylindricalParticleType> WIND_VORTEX = PARTICLES.register(ID_PARTICLE_WIND_VORTEX, CylindricalParticleType::new);

    public static final RegistryObject<PointToPointParticleType> BEAM = PARTICLES.register(ID_PARTICLE_BEAM, PointToPointParticleType::new);
    public static final RegistryObject<PointToPointParticleType> SHARD = PARTICLES.register(ID_PARTICLE_SHARD, PointToPointParticleType::new);
    public static final RegistryObject<PointToPointParticleType> STRAIGHT_ARC = PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, PointToPointParticleType::new);
    public static final RegistryObject<PointToPointParticleType> STREAM = PARTICLES.register(ID_PARTICLE_STREAM, PointToPointParticleType::new);

}
