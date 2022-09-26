package cofh.lib.entity;

import cofh.core.item.KnifeItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

import static cofh.core.util.references.CoreReferences.KNIFE_ENTITY;

public class Knife extends AbstractArrow {

    protected static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Knife.class, EntityDataSerializers.ITEM_STACK);
    protected int hitTime = -1;

    public Knife(EntityType<? extends AbstractArrow> type, Level worldIn) {

        super(type, worldIn);
    }

    public Knife(Level world, double x, double y, double z, ItemStack stack) {

        super(KNIFE_ENTITY, x, y, z, world);
        this.entityData.set(DATA_ITEM_STACK, stack.copy());
    }

    public Knife(Level world, LivingEntity owner, ItemStack stack) {

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
    public Packet<?> getAddEntityPacket() {

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
                    Vec3 diff = owner.getEyePosition(1.0F).subtract(this.position());
                    this.setPosRaw(this.getX(), this.getY() + diff.y * 0.015D * loyalty, this.getZ());
                    if (this.level.isClientSide) {
                        this.yOld = this.getY();
                    }

                    this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(diff.normalize().scale(0.05F * loyalty)));
                } else {
                    if (!this.level.isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                        this.spawnAtLocation(this.getPickupItem(), 0.1F);
                    }
                    this.discard();
                }
            }
        }

        super.tick();
    }

    protected boolean hasReturnOwner() {

        Entity owner = this.getOwner();
        return owner != null && owner.isAlive() && !(owner instanceof ServerPlayer && owner.isSpectator());
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 start, Vec3 end) {

        return this.hitTime >= 0 ? null : super.findHitEntity(start, end);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {

        hitTime = 0;
        super.onHitBlock(result);
        this.setSoundEvent(getDefaultHitGroundSoundEvent());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {

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
                if (target instanceof LivingEntity livingTarget) {
                    if (owner instanceof LivingEntity) {
                        EnchantmentHelper.doPostHurtEffects(livingTarget, owner);
                        EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingTarget);
                        if (owner instanceof Player player) {
                            stack.hurtEnemy(livingTarget, player);
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
    public void playerTouch(Player player) {

        Entity entity = this.getOwner();
        if (entity == null || entity.getUUID() == player.getUUID()) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {

        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Knife", 10)) {
            this.entityData.set(DATA_ITEM_STACK, ItemStack.of(nbt.getCompound("Knife")));
        }
        this.hitTime = nbt.getInt("HitTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {

        super.addAdditionalSaveData(nbt);
        nbt.put("Knife", getPickupItem().save(new CompoundTag()));
        nbt.putInt("HitTime", this.hitTime);
    }

    @Override
    public void tickDespawn() {

        if (this.pickup != AbstractArrow.Pickup.ALLOWED || Utils.getItemEnchantmentLevel(Enchantments.LOYALTY, getPickupItem()) <= 0) {
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
