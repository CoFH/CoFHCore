package cofh.lib.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectType;
import net.minecraft.world.server.ServerWorld;

public abstract class CustomParticleEffect extends EffectCoFH {

    public CustomParticleEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {

        if (!living.level.isClientSide && living.level.random.nextInt(getChance()) == 0) {
            ((ServerWorld) living.level).sendParticles(getParticle(), living.getRandomX(1.0D), living.getRandomY(), living.getRandomZ(1.0D), 1, 0, 0, 0, 0);
            //living.level.addParticle(getParticle(), living.getRandomX(1.1D), living.getRandomY(), living.getRandomZ(1.1D), 0.0D, 0.0D, 0.0D);
        }
    }

    public abstract IParticleData getParticle();

    public int getChance() {

        return 3;
    }
}
