package cofh.core.util.helpers;

import cofh.core.capability.templates.ArcheryAmmoItemWrapper;
import cofh.core.capability.templates.ArcheryBowItemWrapper;
import cofh.core.compat.curios.CuriosProxy;
import cofh.lib.api.capability.IArcheryAmmoItem;
import cofh.lib.api.capability.IArcheryBowItem;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static cofh.core.capability.CapabilityArchery.AMMO_ITEM_CAPABILITY;
import static cofh.core.capability.CapabilityArchery.BOW_ITEM_CAPABILITY;
import static cofh.core.util.references.EnsorcIDs.ID_TRUESHOT;
import static cofh.core.util.references.EnsorcIDs.ID_VOLLEY;
import static cofh.lib.util.Utils.getEnchantment;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.ModIds.ID_ENSORCELLATION;
import static net.minecraft.world.item.enchantment.Enchantments.*;

public final class ArcheryHelper {

    private static final Set<Item> VALID_BOWS = new HashSet<>();

    public static boolean addValidBow(Item bow) {

        return VALID_BOWS.add(bow);
    }

    private ArcheryHelper() {

    }

    public static boolean validBow(ItemStack stack) {

        return VALID_BOWS.contains(stack.getItem()) || stack.getCapability(BOW_ITEM_CAPABILITY).isPresent();
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
    public static boolean fireArrow(ItemStack bow, ItemStack ammo, Player shooter, int charge, Level world) {

        IArcheryBowItem bowCap = bow.getCapability(BOW_ITEM_CAPABILITY).orElse(new ArcheryBowItemWrapper(bow));
        IArcheryAmmoItem ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY).orElse(new ArcheryAmmoItemWrapper(ammo));

        boolean infinite = shooter.getAbilities().instabuild
                || ammoCap.isInfinite(bow, shooter)
                || (isArrow(ammo) && ((ArrowItem) ammo.getItem()).isInfinite(ammo, bow, shooter))
                || ammo.isEmpty() && getItemEnchantmentLevel(INFINITY_ARROWS, bow) > 0;

        if (!ammo.isEmpty() || infinite) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }
            float arrowVelocity = BowItem.getPowerForTime(charge);

            if (arrowVelocity >= 0.1F) {
                if (Utils.isServerWorld(world)) {
                    float accuracyMod = bowCap.getAccuracyModifier(shooter);
                    float damageMod = bowCap.getDamageModifier(shooter);
                    float velocityMod = bowCap.getVelocityModifier(shooter);

                    int encVolley = getItemEnchantmentLevel(getEnchantment(ID_ENSORCELLATION, ID_VOLLEY), bow);
                    int encTrueshot = getItemEnchantmentLevel(getEnchantment(ID_ENSORCELLATION, ID_TRUESHOT), bow);
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
                    float volleyPitch = encVolley > 0 ? MathHelper.clamp(90.0F + shooter.getXRot() / encVolley, 0.0F, 5.0F) : 0;

                    BowItem bowItem = bow.getItem() instanceof BowItem ? (BowItem) bow.getItem() : null;

                    for (int shot = 0; shot < numArrows; ++shot) {
                        AbstractArrow arrow = createArrow(world, ammo, shooter);
                        if (bowItem != null) {
                            arrow = bowItem.customArrow(arrow);
                        }
                        arrow.shootFromRotation(shooter, shooter.getXRot() - volleyPitch * shot, shooter.getYRot(), 0.0F, arrowVelocity * 3.0F * velocityMod, accuracyMod);// * (1 + shot * 2));
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
                            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }
                        world.addFreshEntity(arrow);
                    }
                    bowCap.onArrowLoosed(shooter);
                }
                world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                if (!infinite && !shooter.getAbilities().instabuild) {
                    ammoCap.onArrowLoosed(shooter);
                    if (ammo.isEmpty()) {
                        shooter.getInventory().removeItem(ammo);
                    }
                }
                shooter.awardStat(Stats.ITEM_USED.get(bow.getItem()));
            }
            return true;
        }
        return false;
    }

    public static AbstractArrow createArrow(Level world, ItemStack ammo, Player shooter) {

        LazyOptional<IArcheryAmmoItem> ammoCap = ammo.getCapability(AMMO_ITEM_CAPABILITY);
        return ammoCap.map(cap -> cap.createArrowEntity(world, shooter)).orElse(createDefaultArrow(world, ammo, shooter));
    }

    public static AbstractArrow createDefaultArrow(Level world, ItemStack ammo, Player shooter) {

        return isArrow(ammo) ? ((ArrowItem) ammo.getItem()).createArrow(world, ammo, shooter) : ((ArrowItem) Items.ARROW).createArrow(world, ammo, shooter);
    }

    public static ItemStack findAmmo(Player shooter, ItemStack weapon) {

        ItemStack offHand = shooter.getOffhandItem();
        ItemStack mainHand = shooter.getMainHandItem();
        Predicate<ItemStack> isHeldAmmo = weapon.getItem() instanceof ProjectileWeaponItem weaponItem ? weaponItem.getSupportedHeldProjectiles() : i -> false;
        Predicate<ItemStack> isAmmo = weapon.getItem() instanceof ProjectileWeaponItem weaponItem ? weaponItem.getAllSupportedProjectiles() : i -> false;

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
        for (ItemStack slot : shooter.getInventory().items) {
            if (slot.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isAmmo.test(slot)) {
                return slot;
            }
        }
        // LIVING PROJECTILE EVENT
        ItemStack lpeStack = ForgeHooks.getProjectile(shooter, weapon, ItemStack.EMPTY);
        if (isAmmo.test(lpeStack)) {
            return lpeStack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Called specifically when looking for ARROWS in inventory.
     */
    public static ItemStack findArrows(Player shooter) {

        ItemStack offHand = shooter.getOffhandItem();
        ItemStack mainHand = shooter.getMainHandItem();

        if (isSimpleArrow(offHand)) {
            return offHand;
        } else if (isSimpleArrow(mainHand)) {
            return mainHand;
        }
        for (int i = 0; i < shooter.getInventory().getContainerSize(); ++i) {
            ItemStack stack = shooter.getInventory().getItem(i);
            if (isSimpleArrow(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static Stream<EntityHitResult> findHitEntities(Level world, Projectile projectile, Vec3 startPos, Vec3 endPos, Predicate<Entity> filter) {

        Vec3 padding = new Vec3(projectile.getBbWidth() * 0.5, projectile.getBbHeight() * 0.5, projectile.getBbWidth() * 0.5);
        return findHitEntities(world, projectile, startPos, endPos, projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement()).inflate(1.5D), padding, filter);
    }

    public static Stream<EntityHitResult> findHitEntities(Level world, Entity exclude, Vec3 startPos, Vec3 endPos, Vec3 padding, Predicate<Entity> filter) {

        return findHitEntities(world, exclude, startPos, endPos, new AABB(startPos, endPos).inflate(1.5D), padding, filter);
    }

    public static Stream<EntityHitResult> findHitEntities(Level world, Entity exclude, Vec3 startPos, Vec3 endPos, AABB searchArea, Vec3 padding, Predicate<Entity> filter) {

        return world.getEntities(exclude, searchArea, filter).stream()
                .map(entity -> entity.getBoundingBox().inflate(padding.x(), padding.y(), padding.z()).clip(startPos, endPos).map(Vec3 -> new EntityHitResult(entity, Vec3)).orElse(null))
                .filter(Objects::nonNull);
    }

}
