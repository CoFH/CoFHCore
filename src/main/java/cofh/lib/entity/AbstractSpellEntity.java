package cofh.lib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractSpellEntity extends Entity {

    @Nullable
    protected LivingEntity owner = null;
    protected int duration = 0;

    public AbstractSpellEntity(EntityType<? extends AbstractSpellEntity> type, World world) {

        super(type, world);
    }

    @Override
    public SoundCategory getSoundSource() {

        return owner == null ? SoundCategory.NEUTRAL : owner.getSoundSource();
    }

    @Override
    public void tick() {

        if (firstTick) {
            onCast();
            this.firstTick = false;
        }
        if (tickCount > duration) {
            this.remove();
        }
    }

    public void onCast() {

    }

    public AbstractSpellEntity setOwner(LivingEntity owner) {

        this.owner = owner;
        return this;
    }

    public LivingEntity getOwner() {

        return owner;
    }

    @Override
    public boolean fireImmune() {

        return true;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {

    }

    @Override
    public IPacket<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distSqr) {

        double d0 = 64.0D * getViewScale();
        return distSqr < d0 * d0;
    }

}
