package cofh.core.client.particle.options;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class CylindricalParticleOptions extends ColorParticleOptions {

    public final float height;

    public CylindricalParticleOptions(ParticleType<? extends CylindricalParticleOptions> type, float size, float duration, float delay, int rgba0, float height) {

        super(type, size, duration, delay, rgba0);
        this.height = height;
    }

    public CylindricalParticleOptions(ParticleType<? extends CylindricalParticleOptions> type, float size, float duration, float delay, float height) {

        this(type, size, duration, delay, 0xFFFFFFFF, height);
    }

    public CylindricalParticleOptions(ParticleType<? extends CylindricalParticleOptions> type, float size, float duration, float height) {

        this(type, size, duration, 0.0F, height);
    }

    public CylindricalParticleOptions(ParticleType<? extends CylindricalParticleOptions> type) {

        this(type, 1.0F, 1.0F, 1.0F);
    }

    protected CylindricalParticleOptions(ParticleType<? extends CylindricalParticleOptions> type, StringReader reader) throws CommandSyntaxException {

        super(type, reader);
        reader.expect(' ');
        this.height = (float) reader.readDouble();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {

        super.writeToNetwork(buf);
        buf.writeFloat(height);
    }

    @Override
    public String writeToString() {

        return super.writeToString() + ", " + height;
    }

    public static final Function<ParticleType<CylindricalParticleOptions>, Codec<CylindricalParticleOptions>> CODEC = (type) -> RecordCodecBuilder.create(
            (builder) -> builder.group(
                    Codec.FLOAT.fieldOf("size").forGetter((options) -> options.size),
                    Codec.FLOAT.fieldOf("duration").forGetter((options) -> options.duration),
                    Codec.FLOAT.fieldOf("delay").forGetter((options) -> options.delay),
                    Codec.INT.fieldOf("rgba0").forGetter((options) -> options.rgba0),
                    Codec.FLOAT.fieldOf("height").forGetter((options) -> options.height)
            ).apply(builder, (size, duration, delay, rgba, height) -> new CylindricalParticleOptions(type, size, duration, delay, rgba, height))
    );
    public static final Deserializer<CylindricalParticleOptions> DESERIALIZER = new Deserializer<>() {

        @Override
        @Nonnull
        public CylindricalParticleOptions fromCommand(ParticleType<CylindricalParticleOptions> type, StringReader reader) throws CommandSyntaxException {

            return new CylindricalParticleOptions(type, reader);
        }

        @Override
        @Nonnull
        public CylindricalParticleOptions fromNetwork(ParticleType<CylindricalParticleOptions> type, FriendlyByteBuf buf) {

            return new CylindricalParticleOptions(type, buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readFloat());
        }
    };

}
