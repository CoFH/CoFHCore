package cofh.core.init;

import net.minecraft.core.particles.ParticleType;
import cofh.core.client.particle.types.CylindricalParticleType;
import cofh.core.client.particle.types.PointToPointParticleType;
import cofh.core.client.particle.types.ColorParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.core.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    //public static List<Consumer<ParticleEngine>> factoryRegistration = new ArrayList<>();

    public static void register() {

        //PARTICLES.register(ID_PARTICLE_CURRENT, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_CIRCLE_ARC, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_FLAME, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BLAST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BEAM, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BULLET, () -> new SimpleParticleType(false));
    }

    public static final RegistryObject<ParticleType<SimpleParticleType>> FROST = PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> SPARK = PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> PLASMA = PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> SHOCKWAVE = PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> BLAST_WAVE = PARTICLES.register(ID_PARTICLE_BLAST_WAVE, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> VORTEX = PARTICLES.register(ID_PARTICLE_VORTEX, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> SPIRAL = PARTICLES.register(ID_PARTICLE_SPIRAL, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> STRAIGHT_ARC = PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, () -> new SimpleParticleType(false));
    public static final RegistryObject<ParticleType<SimpleParticleType>> MIST = PARTICLES.register(ID_PARTICLE_MIST, () -> new SimpleParticleType(false));
    
        //PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));

        //PARTICLES.register(ID_PARTICLE_FIRE, ColorParticleType::new);
        //PARTICLES.register(ID_PARTICLE_BLAST, ColorParticleType::new);
        //PARTICLES.register(ID_PARTICLE_MIST, ColorParticleType::new);

        //PARTICLES.register(ID_PARTICLE_BLAST_WAVE, CylindricalParticleType::new);
        //PARTICLES.register(ID_PARTICLE_SHOCKWAVE, CylindricalParticleType::new);
        //PARTICLES.register(ID_PARTICLE_SPIRAL, CylindricalParticleType::new);
        //PARTICLES.register(ID_PARTICLE_VORTEX, CylindricalParticleType::new);

        //PARTICLES.register(ID_PARTICLE_CURRENT, );
        //PARTICLES.register(ID_PARTICLE_BEAM, PointToPointParticleType::new);
        //PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, PointToPointParticleType::new);
        //PARTICLES.register(ID_PARTICLE_SHARD, PointToPointParticleType::new);

        //PARTICLES.register(ID_PARTICLE_CIRCLE_ARC, );
        //PARTICLES.register(ID_PARTICLE_EXPLOSION, );

}
