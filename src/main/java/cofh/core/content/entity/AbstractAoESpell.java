package cofh.core.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class AbstractAoESpell extends AbstractSpell {

    protected float radius = 0;

    public AbstractAoESpell(EntityType<? extends AbstractAoESpell> type, Level level) {

        super(type, level);
    }

    //@Override
    //protected void readAdditionalSaveData(CompoundTag nbt) {
    //
    //    nbt.putFloat(NBTTags.TAG_AUGMENT_RADIUS, radius);
    //}
    //
    //@Override
    //protected void addAdditionalSaveData(CompoundTag nbt) {
    //
    //    radius = nbt.getFloat(NBTTags.TAG_AUGMENT_RADIUS);
    //}

}
