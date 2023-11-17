package cofh.lib.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractAoESpell extends AbstractSpell {

    protected float radius = 0;

    public AbstractAoESpell(EntityType<? extends AbstractAoESpell> type, Level level) {

        super(type, level);
    }

}
