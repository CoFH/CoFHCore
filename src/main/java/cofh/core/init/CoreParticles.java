package cofh.core.init;

import net.minecraft.particles.BasicParticleType;

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    public static void register() {

        //PARTICLES.register(ID_PARTICLE_SNOW, () -> new BasicParticleType(true));
        PARTICLES.register(ID_PARTICLE_FROST, () -> new BasicParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPARK, () -> new BasicParticleType(false));
        PARTICLES.register(ID_PARTICLE_PLASMA, () -> new BasicParticleType(false));
        PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new BasicParticleType(false));
        //PARTICLES.register(ID_PARTICLE_WIND, () -> new BasicParticleType(false));
    }

}
