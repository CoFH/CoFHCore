package cofh.core.client.particle.types;

import cofh.core.client.particle.options.BiColorParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class PointToPointParticleType extends ParticleType<BiColorParticleOptions> {

    public PointToPointParticleType(boolean overrideLimit) {

        super(overrideLimit, BiColorParticleOptions.DESERIALIZER);
    }

    public PointToPointParticleType() {

        this(false);
    }

    @Override
    public Codec<BiColorParticleOptions> codec() {

        return BiColorParticleOptions.CODEC.apply(this);
    }

}
