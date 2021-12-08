package cofh.core.potion;

import cofh.lib.potion.EffectCoFH;
import cofh.lib.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;

public class AmplificationEffect extends EffectCoFH {

    public AmplificationEffect(EffectType typeIn, int liquidColorIn) {

        super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isInstantenous() {

        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {

        // TODO: Revisit if potion logic ever changes. Instant potions don't need this.
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {

        if (entityLivingBaseIn instanceof AnimalEntity) {
            setLoveFlag(indirectSource, (AnimalEntity) entityLivingBaseIn);
        }
    }

    // region HELPERS
    private void setLoveFlag(Entity indirectSource, AnimalEntity animal) {

        PlayerEntity player = indirectSource instanceof PlayerEntity ? (PlayerEntity) indirectSource : null;
        if (animal.getAge() == 0 && !animal.isInLove()) {
            animal.setInLove(player);
            for (int i = 0; i < 4; ++i) {
                Utils.spawnParticles(animal.level, ParticleTypes.HEART, animal.getX() + animal.level.random.nextDouble(), animal.getY() + 1.0D + animal.level.random.nextDouble(), animal.getZ() + animal.level.random.nextDouble(), 1, 0, 0, 0, 0);
            }
        }
    }
    // endregion
}
