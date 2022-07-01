package cofh.core.client.particle.types;

import cofh.core.client.particle.options.ColorParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class RadialParticleType extends ParticleType<ColorParticleOptions> {

    public RadialParticleType(boolean overrideLimit) {

        super(overrideLimit, ColorParticleOptions.DESERIALIZER);
    }

    public RadialParticleType() {

        this(false);
    }

    @Override
    public Codec<ColorParticleOptions> codec() {

        return ColorParticleOptions.CODEC.apply(this);
    }

}
