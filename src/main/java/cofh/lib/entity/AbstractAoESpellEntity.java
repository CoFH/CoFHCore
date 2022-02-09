package cofh.lib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class AbstractAoESpellEntity extends AbstractSpellEntity {

    protected float radius = 0;

    public AbstractAoESpellEntity(EntityType<? extends AbstractAoESpellEntity> type, World world) {

        super(type, world);
    }

    //@Override
    //protected void readAdditionalSaveData(CompoundNBT nbt) {
    //
    //    nbt.putFloat(NBTTags.TAG_AUGMENT_RADIUS, radius);
    //}
    //
    //@Override
    //protected void addAdditionalSaveData(CompoundNBT nbt) {
    //
    //    radius = nbt.getFloat(NBTTags.TAG_AUGMENT_RADIUS);
    //}

}
