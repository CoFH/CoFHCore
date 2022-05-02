package cofh.core.effect;

import cofh.lib.effect.EffectCoFH;
import cofh.lib.util.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class AmplificationEffect extends EffectCoFH {

    public AmplificationEffect(MobEffectCategory typeIn, int liquidColorIn) {

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

        if (entityLivingBaseIn instanceof Animal) {
            setLoveFlag(indirectSource, (Animal) entityLivingBaseIn);
        }
    }

    // region HELPERS
    private void setLoveFlag(Entity indirectSource, Animal animal) {

        Player player = indirectSource instanceof Player ? (Player) indirectSource : null;
        if (animal.getAge() == 0 && !animal.isInLove()) {
            animal.setInLove(player);
            for (int i = 0; i < 4; ++i) {
                Utils.spawnParticles(animal.level, ParticleTypes.HEART, animal.getX() + animal.level.random.nextDouble(), animal.getY() + 1.0D + animal.level.random.nextDouble(), animal.getZ() + animal.level.random.nextDouble(), 1, 0, 0, 0, 0);
            }
        }
    }
    // endregion
}
