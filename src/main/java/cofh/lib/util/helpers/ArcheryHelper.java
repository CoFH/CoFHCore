package cofh.lib.util.helpers;

import cofh.core.compat.curios.CuriosProxy;
import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.capability.IArcheryBowItem;
import cofh.lib.capability.templates.ArcheryAmmoItemWrapper;
import cofh.lib.capability.templates.ArcheryBowItemWrapper;
import cofh.lib.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static cofh.lib.capability.CapabilityArchery.AMMO_ITEM_CAPABILITY;
import static cofh.lib.capability.CapabilityArchery.BOW_ITEM_CAPABILITY;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.references.EnsorcReferences.TRUESHOT;
import static cofh.lib.util.references.EnsorcReferences.VOLLEY;
import static net.minecraft.enchantment.Enchantments.*;

public final class ArcheryHelper {

    private ArcheryHelper() {

    }

    public static boolean validBow(ItemStack stack) {

        return stack.getItem() == Items.BOW || stack.getCapability(BOW_ITEM_CAPABILITY).isPresent();
    }

    public static boolean isArrow(ItemStack stack) {

        return stack.getItem() instanceof ArrowItem;
    }

    public static boolean isSimpleArrow(ItemStack stack) {

        return stack.getItem().equals(Items.ARROW);
    }

    /**
     * Basically the "default" Archery behavior.
     */
    public static boolean fireArrow(ItemStack bow, ItemStack ammo, PlayerEntity shooter, int charge, World world) {

        IArcheryBowItem bowCap = bow.getCapability(BOW_ITEM_CAPABILITY).orElse(new ArcheryBowItemWrapper(bow));
        IArcheryAmmoItem ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY).orElse(new ArcheryAmmoItemWrapper(ammo));

        boolean infinite = shooter.abilities.instabuild
                || ammoCap.isInfinite(bow, shooter)
                || (isArrow(ammo) && ((ArrowItem) ammo.getItem()).isInfinite(ammo, bow, shooter))
                || ammo.isEmpty() && getItemEnchantmentLevel(INFINITY_ARROWS, bow) > 0;

        if (!ammo.isEmpty() || infinite) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }
            float arrowVelocity = BowItem.getPowerForTime(charge);

            float accuracyMod = bowCap.getAccuracyModifier(shooter);
            float damageMod = bowCap.getDamageModifier(shooter);
            float velocityMod = bowCap.getVelocityModifier(shooter);

            if (arrowVelocity >= 0.1F) {
                if (Utils.isServerWorld(world)) {
                    int encVolley = getItemEnchantmentLevel(VOLLEY, bow);
                    int encTrueshot = getItemEnchantmentLevel(TRUESHOT, bow);
                    int encPunch = getItemEnchantmentLevel(PUNCH_ARROWS, bow);
                    int encPower = getItemEnchantmentLevel(POWER_ARROWS, bow);
                    int encFlame = getItemEnchantmentLevel(FLAMING_ARROWS, bow);

                    if (encTrueshot > 0) {
                        accuracyMod *= (1.5F / (1 + encTrueshot));
                        damageMod *= (1.0F + 0.25F * encTrueshot);
                        arrowVelocity = MathHelper.clamp(0.1F, arrowVelocity + 0.05F * encTrueshot, 1.75F);
                    }
                    int numArrows = encVolley > 0 ? 3 : 1;
                    // Each additional arrow fired at a higher arc - arrows will not be fired beyond vertically. Maximum of 5 degrees between arrows.
                    float volleyPitch = encVolley > 0 ? MathHelper.clamp(90.0F + shooter.xRot / encVolley, 0.0F, 5.0F) : 0;

                    BowItem bowItem = bow.getItem() instanceof BowItem ? (BowItem) bow.getItem() : null;

                    for (int shot = 0; shot < numArrows; ++shot) {
                        AbstractArrowEntity arrow = createArrow(world, ammo, shooter);
                        if (bowItem != null) {
                            arrow = bowItem.customArrow(arrow);
                        }
                        arrow.shootFromRotation(shooter, shooter.xRot - volleyPitch * shot, shooter.yRot, 0.0F, arrowVelocity * 3.0F * velocityMod, accuracyMod);// * (1 + shot * 2));
                        arrow.setBaseDamage(arrow.getBaseDamage() * damageMod);

                        if (arrowVelocity >= 1.0F) {
                            arrow.setCritArrow(true);
                        }
                        if (encTrueshot > 0) {
                            arrow.setPierceLevel((byte) encTrueshot);
                        }
                        if (encPower > 0 && arrow.getBaseDamage() > 0) {
                            arrow.setBaseDamage(arrow.getBaseDamage() + (double) encPower * 0.5D + 0.5D);
                        }
                        if (encPunch > 0) {
                            arrow.setKnockback(encPunch);
                        }
                        if (encFlame > 0) {
                            arrow.setSecondsOnFire(100);
                        }
                        if (infinite || shot > 0) {
                            arrow.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        world.addFreshEntity(arrow);
                    }
                    bowCap.onArrowLoosed(shooter);
                }
                world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                if (!infinite && !shooter.abilities.instabuild) {
                    ammoCap.onArrowLoosed(shooter);
                    if (ammo.isEmpty()) {
                        shooter.inventory.removeItem(ammo);
                    }
                }
                shooter.awardStat(Stats.ITEM_USED.get(bow.getItem()));
            }
            return true;
        }
        return false;
    }

    public static AbstractArrowEntity createArrow(World world, ItemStack ammo, PlayerEntity shooter) {

        LazyOptional<IArcheryAmmoItem> ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY);
        return ammoCap.map(cap -> cap.createArrowEntity(world, shooter)).orElse(createDefaultArrow(world, ammo, shooter));
    }

    public static AbstractArrowEntity createDefaultArrow(World world, ItemStack ammo, PlayerEntity shooter) {

        return isArrow(ammo) ? ((ArrowItem) ammo.getItem()).createArrow(world, ammo, shooter) : ((ArrowItem) Items.ARROW).createArrow(world, ammo, shooter);
    }

    public static ItemStack findAmmo(PlayerEntity shooter, ItemStack weapon) {

        ItemStack offHand = shooter.getOffhandItem();
        ItemStack mainHand = shooter.getMainHandItem();
        Predicate<ItemStack> isHeldAmmo = weapon.getItem() instanceof ShootableItem ? ((ShootableItem) weapon.getItem()).getSupportedHeldProjectiles() : i -> false;
        Predicate<ItemStack> isAmmo = weapon.getItem() instanceof ShootableItem ? ((ShootableItem) weapon.getItem()).getAllSupportedProjectiles() : i -> false;

        // HELD
        if (offHand.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isHeldAmmo.test(offHand)) {
            return offHand;
        }
        if (mainHand.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isHeldAmmo.test(mainHand)) {
            return mainHand;
        }
        // CURIOS
        final ItemStack[] retStack = {ItemStack.EMPTY};
        CuriosProxy.getAllWorn(shooter).ifPresent(c -> {
            for (int i = 0; i < c.getSlots(); ++i) {
                ItemStack slot = c.getStackInSlot(i);
                if (slot.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isAmmo.test(slot)) {
                    retStack[0] = slot;
                }
            }
        });
        if (!retStack[0].isEmpty()) {
            return retStack[0];
        }
        // INVENTORY
        for (ItemStack slot : shooter.inventory.items) {
            if (slot.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isAmmo.test(slot)) {
                return slot;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Called specifically when looking for ARROWS in inventory.
     */
    public static ItemStack findArrows(PlayerEntity shooter) {

        ItemStack offHand = shooter.getOffhandItem();
        ItemStack mainHand = shooter.getMainHandItem();

        if (isSimpleArrow(offHand)) {
            return offHand;
        } else if (isSimpleArrow(mainHand)) {
            return mainHand;
        }
        for (int i = 0; i < shooter.inventory.getContainerSize(); ++i) {
            ItemStack stack = shooter.inventory.getItem(i);
            if (isSimpleArrow(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static Stream<EntityRayTraceResult> findHitEntities(World world, ProjectileEntity projectile, Vector3d startPos, Vector3d endPos, Predicate<Entity> filter) {

        Vector3d padding = new Vector3d(projectile.getBbWidth() * 0.5, projectile.getBbHeight() * 0.5, projectile.getBbWidth() * 0.5);
        return findHitEntities(world, projectile, startPos, endPos, projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement()).inflate(1.5D), padding, filter);
    }

    public static Stream<EntityRayTraceResult> findHitEntities(World world, ProjectileEntity projectile, Vector3d startPos, Vector3d endPos, AxisAlignedBB searchArea, Vector3d padding, Predicate<Entity> filter) {

        return world.getEntities(projectile, searchArea, filter).stream()
                .filter(entity -> entity.getBoundingBox().inflate(padding.x(), padding.y(), padding.z()).clip(startPos, endPos).isPresent())
                .map(EntityRayTraceResult::new);
    }

}
