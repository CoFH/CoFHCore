package cofh.lib.entity;

import cofh.lib.item.impl.KnifeItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

import static cofh.lib.util.references.CoreReferences.KNIFE_ENTITY;

public class KnifeEntity extends AbstractArrowEntity {

    protected static final DataParameter<ItemStack> DATA_ITEM_STACK = EntityDataManager.defineId(KnifeEntity.class, DataSerializers.ITEM_STACK);
    protected int hitTime = -1;

    public KnifeEntity(EntityType<? extends AbstractArrowEntity> type, World worldIn) {

        super(type, worldIn);
    }

    public KnifeEntity(World world, double x, double y, double z, ItemStack stack) {

        super(KNIFE_ENTITY, x, y, z, world);
        this.entityData.set(DATA_ITEM_STACK, stack.copy());
    }

    public KnifeEntity(World world, LivingEntity owner, ItemStack stack) {

        super(KNIFE_ENTITY, owner, world);
        this.entityData.set(DATA_ITEM_STACK, stack.copy());
    }

    @Override
    public ItemStack getPickupItem() {

        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    protected void defineSynchedData() {

        super.defineSynchedData();
        this.entityData.define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {

        if (hitTime >= 0) {
            ++hitTime;
        }
        Entity owner = this.getOwner();
        if (hitTime > 4 && owner != null) {
            int loyalty = Utils.getItemEnchantmentLevel(Enchantments.LOYALTY, getPickupItem());
            if (loyalty > 0) {
                if (this.hasReturnOwner()) {
                    if (hitTime == 5) {
                        this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                    }
                    this.setNoPhysics(true);
                    Vector3d diff = owner.getEyePosition(1.0F).subtract(this.position());
                    this.setPosRaw(this.getX(), this.getY() + diff.y * 0.015D * loyalty, this.getZ());
                    if (this.level.isClientSide) {
                        this.yOld = this.getY();
                    }

                    this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(diff.normalize().scale(0.05F * loyalty)));
                } else {
                    if (!this.level.isClientSide && this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED) {
                        this.spawnAtLocation(this.getPickupItem(), 0.1F);
                    }
                    this.remove();
                }
            }
        }

        super.tick();
    }

    protected boolean hasReturnOwner() {

        Entity owner = this.getOwner();
        return owner != null && owner.isAlive() && !(owner instanceof ServerPlayerEntity && owner.isSpectator());
    }

    @Nullable
    protected EntityRayTraceResult findHitEntity(Vector3d start, Vector3d end) {

        return this.hitTime >= 0 ? null : super.findHitEntity(start, end);
    }

    @Override
    protected void onHitBlock(BlockRayTraceResult result) {

        hitTime = 0;
        super.onHitBlock(result);
        this.setSoundEvent(getDefaultHitGroundSoundEvent());
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {

        hitTime = 4;
        Entity target = result.getEntity();
        ItemStack stack = getPickupItem();
        if (stack.getItem() instanceof KnifeItem) {
            float velocity = (float) this.getDeltaMovement().length();
            float damage = ((KnifeItem) stack.getItem()).getDamage();

            damage = (float) MathHelper.clamp(velocity * damage, 0.0D, damage * 3);
            if (target instanceof LivingEntity) {
                damage += EnchantmentHelper.getDamageBonus(stack, ((LivingEntity) target).getMobType());
            }
            Entity owner = this.getOwner();
            if (target.hurt(this.damageSource(), damage)) {
                if (target.getType() == EntityType.ENDERMAN) {
                    return;
                }
                if (target instanceof PartEntity) {
                    target = ((PartEntity<?>) target).getParent();
                }
                if (target instanceof LivingEntity) {
                    LivingEntity livingTarget = (LivingEntity) target;
                    if (owner instanceof LivingEntity) {
                        EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                        EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingTarget);
                        if (owner instanceof PlayerEntity) {
                            stack.hurtEnemy(livingTarget, (PlayerEntity) owner);
                        }
                    }
                    this.doPostHurtEffects(livingTarget);
                    target.setSecondsOnFire(4 * Utils.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack));
                }
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {

        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    public void playerTouch(PlayerEntity player) {

        Entity entity = this.getOwner();
        if (entity == null || entity.getUUID() == player.getUUID()) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {

        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Knife", 10)) {
            this.entityData.set(DATA_ITEM_STACK, ItemStack.of(nbt.getCompound("Knife")));
        }
        this.hitTime = nbt.getInt("HitTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {

        super.addAdditionalSaveData(nbt);
        nbt.put("Knife", getPickupItem().save(new CompoundNBT()));
        nbt.putInt("HitTime", this.hitTime);
    }

    @Override
    public void tickDespawn() {

        if (this.pickup != AbstractArrowEntity.PickupStatus.ALLOWED || Utils.getItemEnchantmentLevel(Enchantments.LOYALTY, getPickupItem()) <= 0) {
            super.tickDespawn();
        }
    }

    public boolean inGround() {

        return this.inGround;
    }

    public DamageSource damageSource() {

        return (new IndirectEntityDamageSource("knife", this, getOwner())).setProjectile();
    }

}
