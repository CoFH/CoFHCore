package cofh.lib.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractTNTEntity extends PrimedTnt {

    protected static final int CLOUD_DURATION = 20;
    protected int radius = 9;

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, Level worldIn) {

        super(type, worldIn);
    }

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, Level worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {

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
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public abstract Block getBlock();

    protected abstract void explode();

}
