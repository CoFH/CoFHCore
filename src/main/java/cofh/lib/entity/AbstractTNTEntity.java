package cofh.lib.entity;

import cofh.lib.block.IDetonatable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractTNTEntity extends TNTEntity implements IDetonatable {

    protected static final int CLOUD_DURATION = 20;
    protected int radius = 9;
    public int effectAmplifier = 1;
    public int effectDuration = 300;

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, World worldIn) {

        super(type, worldIn);
    }

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {

        this(type, worldIn);
        this.setPos(x, y, z);
        double d0 = worldIn.random.nextDouble() * (double) ((float) Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = igniter;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void explode() {

        if (level.isClientSide) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
        } else {
            this.detonate(this.position());
            this.remove();
        }
    }

    public abstract Block getBlock();

}
