package cofh.core.client.particle.types;

import cofh.core.client.particle.options.ColorParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class ColorParticleType extends ParticleType<ColorParticleOptions> {

    public ColorParticleType(boolean overrideLimit) {

        super(overrideLimit, ColorParticleOptions.DESERIALIZER);
    }

    public ColorParticleType() {

        this(false);
    }

    @Override
    public Codec<ColorParticleOptions> codec() {

        return ColorParticleOptions.CODEC.apply(this);
    }

}
