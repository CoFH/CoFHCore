package cofh.lib.item.impl;

import cofh.core.util.ProxyUtils;
import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.capability.templates.ArcheryAmmoItemWrapper;
import cofh.lib.item.ICoFHItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.ArcheryHelper;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static cofh.lib.capability.CapabilityArchery.AMMO_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.CROSSBOW_AMMO;

public class CrossbowItemCoFH extends CrossbowItem implements ICoFHItem {

    protected static Random random = new Random();
    protected int enchantability = 1;
    protected float accuracyModifier = 1.0F;
    protected float damageModifier = 1.0F;
    protected float velocityModifier = 1.0F;

    public CrossbowItemCoFH(Properties builder) {

        super(builder);
        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("pull"), this::getPullModelProperty);
        ProxyUtils.registerItemModelProperty(this, new ResourceLocation("ammo"), this::getAmmoModelProperty);
    }

    public CrossbowItemCoFH(Tier tier, Properties builder) {

        this(builder);
        setParams(tier);
    }

    public CrossbowItemCoFH(int enchantability, float accuracyModifier, float damageModifier, float velocityModifier, Properties builder) {

        this(builder);
        setParams(enchantability, accuracyModifier, damageModifier, velocityModifier);
    }

    public CrossbowItemCoFH setParams(Tier tier) {

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
    @OnlyIn (Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        if (isLoaded(stack)) {
            tooltip.add((new TranslatableComponent("info.cofh.crossbow_loaded")).append(" ").append(getLoadedAmmo(stack).getDisplayName()));
        }
    }

    public float getPullModelProperty(ItemStack stack, Level world, LivingEntity entity, int seed) {

        if (entity == null || !entity.getUseItem().equals(stack)) {
            return 0.0F;
        }
        int baseDuration = getUseDuration(stack);
        int duration = baseDuration - entity.getUseItemRemainingTicks();

        return Mth.clamp((float) (duration) / baseDuration, 0.0F, 1.0F);
    }

    public float getAmmoModelProperty(ItemStack stack, Level world, LivingEntity entity, int seed) {

        if (getLoadedAmmo(stack).isEmpty()) {
            return 0F;
        }
        if (getLoadedAmmo(stack).getItem() instanceof FireworkRocketItem) {
            return 0.5F;
        }
        return 1.0F;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        if (isLoaded(stack)) {
            setLoaded(stack, shootLoadedAmmo(world, player, hand, stack));
            return InteractionResultHolder.consume(stack);
        } else if (!ArcheryHelper.findAmmo(player, stack).isEmpty() || player.getAbilities().instabuild) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {

        return getChargeDuration(stack);
    }

    @Override
    public void onUseTick(Level world, LivingEntity living, ItemStack stack, int durationRemaining) {

        if (!world.isClientSide()) {
            int totalDuration = getUseDuration(stack);
            int duration = totalDuration - durationRemaining;

            if (duration == totalDuration / 4) {
                world.playSound(null, living.getX(), living.getY(), living.getZ(), getStartSound(Utils.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack)), SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (duration == totalDuration / 2) {
                world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity living, int durationRemaining) {

        if (durationRemaining < 0 && !isCharged(stack) && loadAmmo(living, stack)) {
            setCharged(stack, true);
            world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_END, living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    // region HELPER
    public boolean loadAmmo(LivingEntity living, ItemStack crossbow) {

        if (living instanceof Player player) {
            ItemStack ammo = ArcheryHelper.findAmmo(player, crossbow);
            if (!ammo.isEmpty() && ammo.getItem() instanceof FireworkRocketItem) {
                crossbow.getOrCreateTag().put(CROSSBOW_AMMO, ammo.save(new CompoundTag()));
                if (!player.getAbilities().instabuild) {
                    ammo.shrink(1);
                }
                setCharged(crossbow, true);
                return true;
            }
            IArcheryAmmoItem ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY).orElse(new ArcheryAmmoItemWrapper(ammo));
            boolean infinite = player.getAbilities().instabuild
                    || ammoCap.isInfinite(crossbow, player)
                    || (ArcheryHelper.isArrow(ammo) && ((ArrowItem) ammo.getItem()).isInfinite(ammo, crossbow, player));
            if (!ammo.isEmpty() || infinite) {
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }
                crossbow.getOrCreateTag().put(CROSSBOW_AMMO, ammo.save(new CompoundTag()));
                setCharged(crossbow, true);
                if (!infinite) {
                    ammoCap.onArrowLoosed(player);
                    if (ammo.isEmpty()) {
                        player.getInventory().removeItem(ammo);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public ItemStack getLoadedAmmo(ItemStack crossbow) {

        CompoundTag nbt = crossbow.getTag();
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
    public boolean shootLoadedAmmo(Level world, LivingEntity living, InteractionHand hand, ItemStack crossbow) {

        //TODO: dmg/acc/vel modifiers
        if (living instanceof Player shooter) {
            ItemStack ammo = getLoadedAmmo(crossbow);
            if (!ammo.isEmpty()) {
                int multishot = Utils.getItemEnchantmentLevel(Enchantments.MULTISHOT, crossbow);
                int damage = 0;
                for (int i = -multishot; i <= multishot; ++i) {
                    if (!ammo.isEmpty()) {
                        Projectile projectile;
                        if (ammo.getCapability(AMMO_ITEM_CAPABILITY).isPresent() || ammo.getItem() instanceof ArrowItem) {
                            AbstractArrow arrow = ArcheryHelper.createArrow(world, ammo, shooter);
                            projectile = adjustArrow(crossbow, arrow, shooter.getAbilities().instabuild || i != 0);
                            ++damage;
                        } else if (ammo.getItem() instanceof FireworkRocketItem) {
                            projectile = new FireworkRocketEntity(world, ammo, shooter, shooter.getX(), shooter.getEyeY() - (double) 0.15F, shooter.getZ(), true);
                            damage += 3;
                        } else {
                            return true;
                        }

                        world.addFreshEntity(shootProjectile(shooter, projectile, getBaseSpeed(ammo), 1.0F, i * 10.F));
                        float pitch = random.nextFloat() * 0.32F + 0.865F;
                        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pitch);
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

    public AbstractArrow adjustArrow(ItemStack crossbow, AbstractArrow arrow, boolean creativePickup) {

        arrow.setCritArrow(true);
        arrow.setSoundEvent(SoundEvents.CROSSBOW_HIT);
        arrow.setShotFromCrossbow(true);
        int pierce = Utils.getItemEnchantmentLevel(Enchantments.PIERCING, crossbow);
        if (pierce > 0) {
            arrow.setPierceLevel((byte) pierce);
        }
        if (creativePickup) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        return arrow;
    }

    public Projectile shootProjectile(Player shooter, Projectile projectile, float speed, float inaccuracy, float angle) {

        Vector3f vector3f = new Vector3f(shooter.getViewVector(1.0F));
        vector3f.transform(new Quaternion(new Vector3f(shooter.getUpVector(1.0F)), angle, true));
        projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, inaccuracy);
        return projectile;
    }

    // Override to change behavior to e.g. extract energy instead of using durability.
    public void onCrossbowShot(Player shooter, InteractionHand hand, ItemStack crossbow, int damage) {

        crossbow.hurtAndBreak(damage, shooter, (entity) -> entity.broadcastBreakEvent(hand));

        if (shooter instanceof ServerPlayer player) {
            if (!shooter.level.isClientSide()) {
                CriteriaTriggers.SHOT_CROSSBOW.trigger(player, crossbow);
            }
            shooter.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
    }
    // endregion

}
