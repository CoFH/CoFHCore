package cofh.lib.common.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class AbstractAoESpell extends AbstractSpell {

    public AbstractAoESpell(EntityType<? extends AbstractAoESpell> type, Level level) {

        super(type, level);
    }

    public AbstractAoESpell(EntityType<? extends AbstractAoESpell> type, Level level, Vec3 pos, @Nullable Entity owner, float power) {

        super(type, level, pos, owner, power);
    }

    protected abstract float getRadius();

}
