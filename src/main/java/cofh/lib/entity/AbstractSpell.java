package cofh.lib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractSpell extends Entity {

    @Nullable
    protected LivingEntity owner = null;
    protected int duration = 0;

    public AbstractSpell(EntityType<? extends AbstractSpell> type, Level level) {

        super(type, level);
    }

    @Override
    public SoundSource getSoundSource() {

        return owner == null ? SoundSource.NEUTRAL : owner.getSoundSource();
    }

    @Override
    public void tick() {

        if (firstTick) {
            onCast();
            this.firstTick = false;
        }
        if (!level.isClientSide() && tickCount > duration) {
            this.discard();
        } else {
            activeTick();
        }
    }

    public void onCast() {

    }

    public void activeTick() {

    }

    public AbstractSpell setOwner(LivingEntity owner) {

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
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean shouldRenderAtSqrDistance(double distSqr) {

        double d0 = 64.0D * getViewScale();
        return distSqr < d0 * d0;
    }

}
