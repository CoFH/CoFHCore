package cofh.lib.entity;

import cofh.core.util.helpers.ArcheryHelper;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.raytracer.MissResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class ProjectileCoFH extends Projectile {

    public ProjectileCoFH(EntityType<? extends Projectile> type, Level level) {

        super(type, level);
    }

    public ProjectileCoFH(EntityType<? extends Projectile> type, Level level, Entity owner, Vec3 position, Vec3 velocity) {

        this(type, level);
        setOwner(owner);
        setPos(position);
        setDeltaMovement(velocity);
        projectileTick(level);
        leftOwner = true;
        gameEvent(GameEvent.PROJECTILE_SHOOT, owner);
        hasBeenShot = true;
        updateVelocity();
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public Packet<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean canAddPassenger(Entity entity) {

        return false;
    }

    @Override
    public boolean canRide(Entity entity) {

        return false;
    }

    public Player getPlayerOwner() {
        Entity owner = getOwner();
        return owner instanceof Player ? (Player) owner : null;
    }

    @Override
    public void tick() {

        this.walkDistO = this.walkDist;
        handleNetherPortal();

        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;

        updateInWaterStateAndDoFluidPushing();
        fireTick();
        checkOutOfWorld();
        setOldPosAndRot();
        projectileTick(level);
        checkInsideBlocks();
        updateVelocity();

        this.firstTick = false;
    }

    /**
     * Performs entity and block raycasts and handles collisions/movement.
     */
    public void projectileTick(Level level) {

        Vec3 start = this.position();
        Vec3 end = start.add(this.getDeltaMovement());
        BlockHitResult blockResult = blockRayTrace(level, start, end);
        if (blockResult.getType() != HitResult.Type.MISS) {
            end = blockResult.getLocation();
        }
        HitResult entityCollision = hitEntities(level, start, end);
        if (entityCollision.getType() == HitResult.Type.MISS) {
            BlockPos blockpos = blockResult.getBlockPos();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity gateway && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, gateway);
                }
            } else if (!ForgeEventFactory.onProjectileImpact(this, blockResult)) {
                this.onHitBlock(blockResult);
            }
        } else {
            this.setPos(entityCollision.getLocation());
        }
    }

    public BlockHitResult blockRayTrace(Level level, Vec3 startPos, Vec3 endPos) {

        return level.clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    /**
     * Replacement for onHitEntity that can handle multiple entities.
     * @return HitResult with the new location of the projectile.
     */
    public HitResult hitEntities(Level level, Vec3 startPos, Vec3 endPos) {

        Vec3 pos = this.position();
        Optional<EntityHitResult> closest = entityRayTrace(level, startPos, endPos)
                .sorted(Comparator.comparingDouble(result -> result.getLocation().distanceToSqr(pos)))
                .filter(result -> !ForgeEventFactory.onProjectileImpact(this, result))
                .findFirst();
        if (closest.isPresent()) {
            onHitEntity(closest.get());
            return closest.get();
        }
        return new MissResult(endPos);
    }

    public Stream<EntityHitResult> entityRayTrace(Level level, Vec3 startPos, Vec3 endPos) {

        return ArcheryHelper.findHitEntities(level, this, startPos, endPos, this::canHitEntity);
    }

    @Override
    public boolean canHitEntity(Entity entity) {

        return super.canHitEntity(entity);
    }

    /**
     * Updates the velocity and rotation of the projectile. Gravity/acceleration effects go here.
     */
    public void updateVelocity() {

        Vec3 velocity = this.getDeltaMovement();
        this.setXRot((float) (Mth.atan2(velocity.y, velocity.horizontalDistance()) * MathHelper.TO_DEG));
        this.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * MathHelper.TO_DEG));
    }

    public void fireTick() {

        boolean inLava = this.isInLava();
        if (inLava) {
            this.lavaHurt();
            this.fallDistance *= this.getFluidFallDistanceModifier(ForgeMod.LAVA_TYPE.get());
        }

        if (this.level.isClientSide) {
            this.clearFire();
        } else {
            if (this.remainingFireTicks > 0) {
                if (this.fireImmune()) {
                    this.clearFire();
                } else {
                    if (this.remainingFireTicks % 20 == 0 && !inLava) {
                        this.hurt(DamageSource.ON_FIRE, 1.0F);
                    }

                    this.setRemainingFireTicks(this.remainingFireTicks - 1);
                }

                if (this.getTicksFrozen() > 0) {
                    this.setTicksFrozen(0);
                    this.level.levelEvent(null, 1009, this.blockPosition(), 1);
                }
            }
            this.setSharedFlagOnFire(this.remainingFireTicks > 0);
        }
    }

}
