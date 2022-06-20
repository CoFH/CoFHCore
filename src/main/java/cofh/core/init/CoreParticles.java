package cofh.core.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.core.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    public static void register() {

        FROST = PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
        SPARK = PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));
        PLASMA = PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
        SHOCKWAVE = PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new SimpleParticleType(false));
        BLAST_WAVE = PARTICLES.register(ID_PARTICLE_BLAST_WAVE, () -> new SimpleParticleType(false));
        VORTEX = PARTICLES.register(ID_PARTICLE_VORTEX, () -> new SimpleParticleType(false));
        SPIRAL = PARTICLES.register(ID_PARTICLE_SPIRAL, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_CURRENT, () -> new SimpleParticleType(false));
        STRAIGHT_ARC = PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_CIRCLE_ARC, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_FLAME, () -> new SimpleParticleType(false));
        MIST = PARTICLES.register(ID_PARTICLE_MIST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BLAST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BEAM, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BULLET, () -> new SimpleParticleType(false));
    }

    public static RegistryObject<ParticleType<SimpleParticleType>> FROST;
    public static RegistryObject<ParticleType<SimpleParticleType>> SPARK;
    public static RegistryObject<ParticleType<SimpleParticleType>> PLASMA;
    public static RegistryObject<ParticleType<SimpleParticleType>> SHOCKWAVE;
    public static RegistryObject<ParticleType<SimpleParticleType>> BLAST_WAVE;
    public static RegistryObject<ParticleType<SimpleParticleType>> VORTEX;
    public static RegistryObject<ParticleType<SimpleParticleType>> SPIRAL;
    public static RegistryObject<ParticleType<SimpleParticleType>> STRAIGHT_ARC;
    public static RegistryObject<ParticleType<SimpleParticleType>> MIST;

}
