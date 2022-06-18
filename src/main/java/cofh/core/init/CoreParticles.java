package cofh.core.init;

<<<<<<< HEAD
import cofh.core.client.particle.*;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
=======
import net.minecraft.core.particles.SimpleParticleType;
>>>>>>> parent of 4283c98 (mist particle, particle blend shader)

import static cofh.core.CoFHCore.PARTICLES;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreParticles {

    private CoreParticles() {

    }

    public static void register() {

        PARTICLES.register(ID_PARTICLE_FROST, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPARK, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_PLASMA, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SHOCKWAVE, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_BLAST_WAVE, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_VORTEX, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_SPIRAL, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_CURRENT, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_STRAIGHT_ARC, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_CIRCLE_ARC, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_FLAME, () -> new SimpleParticleType(false));
        PARTICLES.register(ID_PARTICLE_MIST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BLAST, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BEAM, () -> new SimpleParticleType(false));
        //PARTICLES.register(ID_PARTICLE_BULLET, () -> new SimpleParticleType(false));
    }

}
