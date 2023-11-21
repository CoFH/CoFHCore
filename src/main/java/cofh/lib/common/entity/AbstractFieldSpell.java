package cofh.lib.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;

import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

import static cofh.lib.util.constants.NBTTags.TAG_AUGMENT_RADIUS;
import static cofh.lib.util.constants.NBTTags.TAG_DURATION;

public abstract class AbstractFieldSpell extends AbstractAoESpell implements IEntityAdditionalSpawnData {

    protected RandomGenerator rand = new SplittableRandom();
    protected int duration;
    protected float radius;

    public AbstractFieldSpell(EntityType<? extends AbstractFieldSpell> type, Level level) {

        super(type, level);
    }

    public AbstractFieldSpell(EntityType<? extends AbstractFieldSpell> type, Level level, Vec3 pos, @Nullable Entity owner, float power, int duration, float radius) {

        super(type, level, pos, owner, power);
        this.duration = duration;
        setRadius(radius);
    }

    @Override
    protected int getDuration() {

        return duration;
    }

    @Override
    public float getRadius() {

        return radius;
    }

    protected void setRadius(float radius) {

        this.radius = radius;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {

        buffer.writeVarInt(getDuration());
        buffer.writeFloat(getRadius());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {

        duration = additionalData.readVarInt();
        setRadius(additionalData.readFloat());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

        super.addAdditionalSaveData(tag);
        tag.putInt(TAG_DURATION, duration);
        tag.putFloat(TAG_AUGMENT_RADIUS, getRadius());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

        super.readAdditionalSaveData(tag);
        this.duration = tag.getInt(TAG_DURATION);
        setRadius(tag.getFloat(TAG_AUGMENT_RADIUS));
    }

}
