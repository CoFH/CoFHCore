package cofh.lib.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class AbstractTNTEntity extends TNTEntity {

    protected static final int CLOUD_DURATION = 20;
    protected int radius = 9;

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, World worldIn) {

        super(type, worldIn);
    }

    public AbstractTNTEntity(EntityType<? extends AbstractTNTEntity> type, World worldIn, double x, double y, double z, @Nullable LivingEntity igniter) {

        this(type, worldIn);
        this.setPosition(x, y, z);
        double d0 = worldIn.rand.nextDouble() * (double) ((float) Math.PI * 2F);
        this.setMotion(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
        this.setFuse(80);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.tntPlacedBy = igniter;
    }

    @Override
    public IPacket<?> createSpawnPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public abstract Block getBlock();

    protected abstract void explode();

}
