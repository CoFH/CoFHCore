package cofh.lib.item.impl;

import cofh.core.util.ProxyUtils;
import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.capability.templates.ArcheryAmmoItemWrapper;
import cofh.lib.item.ICoFHItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.ArcheryHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.lib.capability.CapabilityArchery.AMMO_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.CROSSBOW_AMMO;

public class CrossbowItemCoFH extends CrossbowItem implements ICoFHItem {

    protected int enchantability = 1;
    protected float accuracyModifier = 1.0F;
    protected float damageModifier = 1.0F;
    protected float velocityModifier = 1.0F;

    public CrossbowItemCoFH(Properties builder) {

        super(builder);
        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("pull"), this::getPullModelProperty);
        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("ammo"), this::getAmmoModelProperty);
    }

    public CrossbowItemCoFH(IItemTier tier, Properties builder) {

        this(builder);
        setParams(tier);
    }

    public CrossbowItemCoFH(int enchantability, float accuracyModifier, float damageModifier, float velocityModifier, Properties builder) {

        this(builder);
        setParams(enchantability, accuracyModifier, damageModifier, velocityModifier);
    }

    public CrossbowItemCoFH setParams(IItemTier tier) {

        this.enchantability = tier.getEnchantmentValue();
        this.damageModifier = tier.getAttackDamageBonus() / 4;
        this.velocityModifier = tier.getSpeed() / 20;
        return this;
    }

    public CrossbowItemCoFH setParams(int enchantability, float accuracyModifier, float damageModifier, float velocityModifier) {

        this.enchantability = enchantability;
        this.accuracyModifier = accuracyModifier;
        this.damageModifier = damageModifier;
        this.velocityModifier = velocityModifier;
        return this;
    }

    @Override
    public int getEnchantmentValue() {

        return enchantability;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        if (isLoaded(stack)) {
            tooltip.add((new TranslationTextComponent("info.cofh.crossbow_loaded")).append(" ").append(getLoadedAmmo(stack).getDisplayName()));
        }
    }

    public float getPullModelProperty(ItemStack stack, World world, LivingEntity entity) {

        if (entity == null || !entity.getUseItem().equals(stack)) {
            return 0.0F;
        }
        int baseDuration = getUseDuration(stack);
        int duration = baseDuration - entity.getUseItemRemainingTicks();

        return MathHelper.clamp((float) (duration) / baseDuration, 0.0F, 1.0F);
    }

    public float getAmmoModelProperty(ItemStack stack, World world, LivingEntity entity) {

        if (getLoadedAmmo(stack).isEmpty()) {
            return 0F;
        }
        if (getLoadedAmmo(stack).getItem() instanceof FireworkRocketItem) {
            return 0.5F;
        }
        return 1.0F;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getItemInHand(hand);
        if (isLoaded(stack)) {
            setLoaded(stack, shootLoadedAmmo(world, player, hand, stack));
            return ActionResult.consume(stack);
        } else if (!ArcheryHelper.findAmmo(player, stack).isEmpty() || player.abilities.instabuild) {
            player.startUsingItem(hand);
            return ActionResult.consume(stack);
        }
        return ActionResult.fail(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return getChargeDuration(stack);
    }

    @Override
    public void onUseTick(World world, LivingEntity living, ItemStack stack, int durationRemaining) {

        if (!world.isClientSide()) {
            int totalDuration = getUseDuration(stack);
            int duration = totalDuration - durationRemaining;

            if (duration == totalDuration / 4) {
                world.playSound(null, living.getX(), living.getY(), living.getZ(), getStartSound(Utils.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack)), SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
            if (duration == totalDuration / 2) {
                world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity living, int durationRemaining) {

        if (durationRemaining < 0 && !isCharged(stack) && loadAmmo(living, stack)) {
            setCharged(stack, true);
            world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_END, living instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    // region HELPER
    public boolean loadAmmo(LivingEntity living, ItemStack crossbow) {

        if (living instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) living;
            ItemStack ammo = ArcheryHelper.findAmmo(player, crossbow);
            if (!ammo.isEmpty() && ammo.getItem() instanceof FireworkRocketItem) {
                crossbow.getOrCreateTag().put(CROSSBOW_AMMO, ammo.save(new CompoundNBT()));
                if (!player.abilities.instabuild) {
                    ammo.shrink(1);
                }
                setCharged(crossbow, true);
                return true;
            }
            IArcheryAmmoItem ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY).orElse(new ArcheryAmmoItemWrapper(ammo));
            boolean infinite = player.abilities.instabuild
                    || ammoCap.isInfinite(crossbow, player)
                    || (ArcheryHelper.isArrow(ammo) && ((ArrowItem) ammo.getItem()).isInfinite(ammo, crossbow, player));
            if (!ammo.isEmpty() || infinite) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }
                crossbow.getOrCreateTag().put(CROSSBOW_AMMO, ammo.save(new CompoundNBT()));
                setCharged(crossbow, true);
                if (!infinite) {
                    ammoCap.onArrowLoosed(player);
                    if (ammo.isEmpty()) {
                        player.inventory.removeItem(ammo);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public ItemStack getLoadedAmmo(ItemStack crossbow) {

        CompoundNBT nbt = crossbow.getTag();
        if (nbt != null && nbt.contains(CROSSBOW_AMMO)) {
            return ItemStack.of(nbt.getCompound(CROSSBOW_AMMO));
        }
        return ItemStack.EMPTY;
    }

    // Overrideable forms of isCharged() and setCharged() in CrossbowItem.
    public void setLoaded(ItemStack crossbow, boolean loaded) {

        CrossbowItem.setCharged(crossbow, loaded);
    }

    public boolean isLoaded(ItemStack crossbow) {

        return CrossbowItem.isCharged(crossbow);
    }

    // Returns true if the crossbow should still be charged after this method is called.
    public boolean shootLoadedAmmo(World world, LivingEntity living, Hand hand, ItemStack crossbow) {

        //TODO: dmg/acc/vel modifiers
        if (living instanceof PlayerEntity) {
            PlayerEntity shooter = (PlayerEntity) living;
            ItemStack ammo = getLoadedAmmo(crossbow);
            if (!ammo.isEmpty()) {
                int multishot = Utils.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow);
                int damage = 0;
                for (int i = -multishot; i <= multishot; ++i) {
                    if (!ammo.isEmpty()) {
                        ProjectileEntity projectile;
                        if (ammo.getCapability(AMMO_ITEM_CAPABILITY).isPresent() || ammo.getItem() instanceof ArrowItem) {
                            AbstractArrowEntity arrow = ArcheryHelper.createArrow(world, ammo, shooter);
                            projectile = adjustArrow(crossbow, arrow, shooter.abilities.instabuild || i != 0);
                            ++damage;
                        } else if (ammo.getItem() instanceof FireworkRocketItem) {
                            projectile = new FireworkRocketEntity(world, ammo, shooter, shooter.getX(), shooter.getEyeY() - (double) 0.15F, shooter.getZ(), true);
                            damage += 3;
                        } else {
                            return true;
                        }

                        world.addFreshEntity(shootProjectile(shooter, projectile, getBaseSpeed(ammo), 1.0F, i * 10.F));
                        float pitch = random.nextFloat() * 0.32F + 0.865F;
                        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, pitch);
                    }
                }

                onCrossbowShot(shooter, hand, crossbow, damage);
            }
        }
        crossbow.removeTagKey(CROSSBOW_AMMO);
        return false;
    }

    public float getBaseSpeed(ItemStack ammo) {

        return ammo.getItem() instanceof FireworkRocketItem ? 1.6F : 3.15F;
    }

    public AbstractArrowEntity adjustArrow(ItemStack crossbow, AbstractArrowEntity arrow, boolean creativePickup) {

        arrow.setCritArrow(true);
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrow.setShotFromCrossbow(true);
        int pierce = Utils.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (pierce > 0) {
            arrow.setPierceLevel((byte) pierce);
        }
        if (creativePickup) {
            arrow.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }
        return arrow;
    }

    public ProjectileEntity shootProjectile(PlayerEntity shooter, ProjectileEntity projectile, float speed, float inaccuracy, float angle) {

        Vector3f vector3f = new Vector3f(shooter.getViewVector(1.0F));
        vector3f.transform(new Quaternion(new Vector3f(shooter.getUpVector(1.0F)), angle, true));
        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, inaccuracy);
        return projectile;
    }

    // Override to change behavior to e.g. extract energy instead of using durability.
    public void onCrossbowShot(PlayerEntity shooter, Hand hand, ItemStack crossbow, int damage) {

        crossbow.hurtAndBreak(damage, shooter, (entity) -> entity.broadcastBreakEvent(hand));

        if (shooter instanceof ServerPlayerEntity) {
            if (!shooter.level.isClientSide()) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger((ServerPlayerEntity) shooter, crossbow);
            }
            shooter.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
    }
    // endregion

}
