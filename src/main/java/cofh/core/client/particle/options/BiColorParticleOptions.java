package cofh.core.client.particle.options;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class BiColorParticleOptions extends ColorParticleOptions {

    public final int rgba1;

    public BiColorParticleOptions(ParticleType<? extends BiColorParticleOptions> type, float size, float duration, float delay, int rgba0, int rgba1) {

        super(type, size, duration, delay, rgba0);
        this.rgba1 = rgba1;
    }

    public BiColorParticleOptions(ParticleType<? extends BiColorParticleOptions> type, float size, float duration, float delay) {

        this(type, size, duration, delay, 0xFFFFFFFF, 0xFFFFFFFF);
    }

    public BiColorParticleOptions(ParticleType<? extends BiColorParticleOptions> type, float size, float duration) {

        this(type, size, duration, 0.0F);
    }

    public BiColorParticleOptions(ParticleType<? extends BiColorParticleOptions> type) {

        this(type, 1.0F, 1.0F, 0.0F);
    }

    protected BiColorParticleOptions(ParticleType<? extends BiColorParticleOptions> type, StringReader reader) throws CommandSyntaxException {

        super(type, reader);
        reader.expect(' ');
        this.rgba1 = reader.readInt();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {

        super.writeToNetwork(buf);
        buf.writeInt(rgba1);
    }

    @Override
    public String writeToString() {

        return super.writeToString() + ", " + String.format("0x%08X", rgba1);
    }

    public static final Function<ParticleType<BiColorParticleOptions>, Codec<BiColorParticleOptions>> CODEC = (type) -> RecordCodecBuilder.create(
            (builder) -> builder.group(
                    Codec.FLOAT.fieldOf("size").forGetter((options) -> options.size),
                    Codec.FLOAT.fieldOf("duration").forGetter((options) -> options.duration),
                    Codec.FLOAT.fieldOf("delay").forGetter((options) -> options.delay),
                    Codec.INT.fieldOf("rgba0").forGetter((options) -> options.rgba0),
                    Codec.INT.fieldOf("rgba1").forGetter((options) -> options.rgba1)
            ).apply(builder, (size, duration, delay, rgba0, rgba1) -> new BiColorParticleOptions(type, size, duration, delay, rgba0, rgba1))
    );
    public static final Deserializer<BiColorParticleOptions> DESERIALIZER = new Deserializer<>() {

        @Override
        @Nonnull
        public BiColorParticleOptions fromCommand(ParticleType<BiColorParticleOptions> type, StringReader reader) throws CommandSyntaxException {

            return new BiColorParticleOptions(type, reader);
        }

        @Override
        @Nonnull
        public BiColorParticleOptions fromNetwork(ParticleType<BiColorParticleOptions> type, FriendlyByteBuf buf) {

            return new BiColorParticleOptions(type, buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt());
        }
    };

}
