package cofh.core.client.particle.types;

import cofh.core.client.particle.options.CylindricalParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class CylindricalParticleType extends ParticleType<CylindricalParticleOptions> {

    public CylindricalParticleType(boolean overrideLimit) {

        super(overrideLimit, CylindricalParticleOptions.DESERIALIZER);
    }

    public CylindricalParticleType() {

        this(false);
    }

    @Override
    public Codec<CylindricalParticleOptions> codec() {

        return CylindricalParticleOptions.CODEC.apply(this);
    }

}
