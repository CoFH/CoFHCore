package cofh.lib.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static cofh.lib.util.constants.NBTTags.TAG_FUSE;

public abstract class AbstractTNTMinecartEntity extends AbstractMinecartEntityCoFH {

    protected static final int CLOUD_DURATION = 20;

    protected int radius = 8;
    protected int fuse = -1;
    protected boolean detonated = false;

    public AbstractTNTMinecartEntity(EntityType<?> type, World worldIn) {

        super(type, worldIn);
    }

    public AbstractTNTMinecartEntity(EntityType<?> type, World worldIn, double posX, double posY, double posZ) {

        super(type, worldIn, posX, posY, posZ);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {

        return getBlock().defaultBlockState();
    }

    @Override
    public void tick() {

        super.tick();

        if (this.fuse > 0) {
            --this.fuse;
            this.level.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
        } else if (this.fuse == 0) {
            this.explodeCart(getHorizontalDistanceSqr(this.getDeltaMovement()));
        }
        if (this.horizontalCollision) {
            double d0 = getHorizontalDistanceSqr(this.getDeltaMovement());
            if (d0 >= (double) 0.01F) {
                this.explodeCart(d0);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrowEntity) {
            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) entity;
            if (abstractarrowentity.isOnFire()) {
                this.explodeCart(abstractarrowentity.getDeltaMovement().lengthSqr());
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void destroy(DamageSource source) {

        double d0 = getHorizontalDistanceSqr(this.getDeltaMovement());
        if (!source.isFire() && !source.isExplosion() && !(d0 >= (double) 0.01F)) {
            detonated = true;
            super.destroy(source);
            if (!source.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation(getBlock());
            }
        } else {
            if (this.fuse < 0) {
                this.ignite();
                this.fuse = this.random.nextInt(20) + this.random.nextInt(20);
            }
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {

        if (distance >= 3.0F) {
            float f = distance / 10.0F;
            this.explodeCart(f * f);
        }
        return super.causeFallDamage(distance, damageMultiplier);
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean receivingPower) {

        if (receivingPower && this.fuse < 0) {
            this.ignite();
        }
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public void handleEntityEvent(byte id) {

        if (id == 10) {
            this.ignite();
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, FluidState p_180428_5_, float p_180428_6_) {

        return !this.isIgnited() || !blockStateIn.is(BlockTags.RAILS) && !worldIn.getBlockState(pos.above()).is(BlockTags.RAILS) ? super.getBlockExplosionResistance(explosionIn, worldIn, pos, blockStateIn, p_180428_5_, p_180428_6_) : 0.0F;
    }

    @Override
    public boolean shouldBlockExplode(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float p_174816_5_) {

        return (!this.isIgnited() || !blockStateIn.is(BlockTags.RAILS) && !worldIn.getBlockState(pos.above()).is(BlockTags.RAILS)) && super.shouldBlockExplode(explosionIn, worldIn, pos, blockStateIn, p_174816_5_);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {

        super.readAdditionalSaveData(compound);

        if (compound.contains(TAG_FUSE, 99)) {
            this.fuse = compound.getInt(TAG_FUSE);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {

        super.addAdditionalSaveData(compound);

        compound.putInt(TAG_FUSE, this.fuse);
    }

    @Override
    public Type getMinecartType() {

        return AbstractMinecartEntity.Type.TNT;
    }

    public void ignite() {

        this.fuse = 80;
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte) 10);
            if (!this.isSilent()) {
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public int getFuseTicks() {

        return this.fuse;
    }

    public boolean isIgnited() {

        return this.fuse > -1;
    }

    public abstract Block getBlock();

    protected void explodeCart(double speed) {

        double d0 = Math.sqrt(speed);
        if (d0 > 5.0D) {
            d0 = 5.0D;
        }
        radius += (int) level.random.nextDouble() * 1.5 * d0;
        detonated = true;
        explode();
    }

    protected abstract void explode();

}
