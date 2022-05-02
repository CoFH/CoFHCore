package cofh.core.init;

import net.minecraft.core.particles.SimpleParticleType;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    public static void register() {

        //PARTICLES.register(ID_PARTICLE_SNOW, () -> new SimpleParticleType(true));
        PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_BLAST_WAVE, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_VORTEX, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPIRAL, () -> new SimpleParticleType(false));
    }

}
