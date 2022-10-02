package cofh.core.client.particle.options;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ColorParticleOptions extends CoFHParticleOptions {

    public final int rgba0;

    public ColorParticleOptions(ParticleType<? extends ColorParticleOptions> type, float size, float duration, int rgba) {

        super(type, size, duration);
        this.rgba0 = rgba;
    }

    public ColorParticleOptions(ParticleType<? extends ColorParticleOptions> type, float size, float duration) {

        this(type, size, duration, 0xFFFFFFFF);
    }

    public ColorParticleOptions(ParticleType<? extends ColorParticleOptions> type) {

        this(type, 1.0F, 1.0F, 0xFFFFFFFF);
    }

    protected ColorParticleOptions(ParticleType<? extends ColorParticleOptions> type, StringReader reader) throws CommandSyntaxException {

        super(type, reader);
        reader.expect(' ');
        this.rgba0 = reader.readInt();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {

        super.writeToNetwork(buf);
        buf.writeInt(rgba0);
    }

    @Override
    public String writeToString() {

        return super.writeToString() + ", " + String.format("0x%08X", rgba0);
    }

    public static final Function<ParticleType<ColorParticleOptions>, Codec<ColorParticleOptions>> CODEC = (type) -> RecordCodecBuilder.create(
            (builder) -> builder.group(
                    Codec.FLOAT.fieldOf("size").forGetter((options) -> options.size),
                    Codec.FLOAT.fieldOf("duration").forGetter((options) -> options.duration),
                    Codec.INT.fieldOf("rgba0").forGetter((options) -> options.rgba0)
            ).apply(builder, (size, duration, rgba) -> new ColorParticleOptions(type, size, duration, rgba))
    );
    public static final ParticleOptions.Deserializer<ColorParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {

        @Override
        @Nonnull
        public ColorParticleOptions fromCommand(ParticleType<ColorParticleOptions> type, StringReader reader) throws CommandSyntaxException {

            return new ColorParticleOptions(type, reader);
        }

        @Override
        @Nonnull
        public ColorParticleOptions fromNetwork(ParticleType<ColorParticleOptions> type, FriendlyByteBuf buf) {

            return new ColorParticleOptions(type, buf.readFloat(), buf.readFloat(), buf.readInt());
        }
    };

}
