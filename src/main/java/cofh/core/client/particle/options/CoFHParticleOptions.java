package cofh.core.client.particle.options;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class CoFHParticleOptions implements ParticleOptions {

    public final ParticleType<? extends CoFHParticleOptions> type;
    public final float size;
    public final float duration;

    public CoFHParticleOptions(ParticleType<? extends CoFHParticleOptions> type, float size, float duration) {

        this.type = type;
        this.size = size;
        this.duration = duration;
    }

    public CoFHParticleOptions(ParticleType<? extends ColorParticleOptions> type) {

        this(type, 1.0F, 1.0F);
    }

    protected CoFHParticleOptions(ParticleType<? extends CoFHParticleOptions> type, StringReader reader) throws CommandSyntaxException {

        this.type = type;
        reader.expect(' ');
        this.size = (float) reader.readDouble();
        reader.expect(' ');
        this.duration = (float) reader.readDouble();
    }

    @Override
    public ParticleType<? extends CoFHParticleOptions> getType() {

        return type;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {

        buf.writeFloat(size);
        buf.writeFloat(duration);
    }

    @Override
    public String writeToString() {

        return size + ", " + duration;
    }

}
