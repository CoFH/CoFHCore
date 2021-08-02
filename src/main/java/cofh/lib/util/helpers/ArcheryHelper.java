package cofh.lib.util.helpers;

import cofh.lib.capability.IArcheryAmmoItem;
import cofh.lib.capability.IArcheryBowItem;
import cofh.lib.capability.templates.ArcheryAmmoItemWrapper;
import cofh.lib.capability.templates.ArcheryBowItemWrapper;
import cofh.lib.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

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

        boolean infinite = shooter.abilities.isCreativeMode
                || ammoCap.isInfinite(bow, shooter)
                || (isArrow(ammo) && ((ArrowItem) ammo.getItem()).isInfinite(ammo, bow, shooter))
                || ammo.isEmpty() && getItemEnchantmentLevel(INFINITY, bow) > 0;

        if (!ammo.isEmpty() || infinite) {
            if (ammo.isEmpty()) {
                ammo = new ItemStack(Items.ARROW);
            }
            float arrowVelocity = BowItem.getArrowVelocity(charge);

            float accuracyMod = bowCap.getAccuracyModifier(shooter);
            float damageMod = bowCap.getDamageModifier(shooter);
            float velocityMod = bowCap.getVelocityModifier(shooter);

            if (arrowVelocity >= 0.1F) {
                if (Utils.isServerWorld(world)) {
                    int encVolley = getItemEnchantmentLevel(VOLLEY, bow);
                    int encTrueshot = getItemEnchantmentLevel(TRUESHOT, bow);
                    int encPunch = getItemEnchantmentLevel(PUNCH, bow);
                    int encPower = getItemEnchantmentLevel(POWER, bow);
                    int encFlame = getItemEnchantmentLevel(FLAME, bow);

                    if (encTrueshot > 0) {
                        accuracyMod *= (1.5F / (1 + encTrueshot));
                        damageMod *= (1.0F + 0.25F * encTrueshot);
                        arrowVelocity = MathHelper.clamp(0.1F, arrowVelocity + 0.05F * encTrueshot, 1.75F);
                    }
                    int numArrows = encVolley > 0 ? 3 : 1;
                    // Each additional arrow fired at a higher arc - arrows will not be fired beyond vertically. Maximum of 5 degrees between arrows.
                    float volleyPitch = encVolley > 0 ? MathHelper.clamp(90.0F + shooter.rotationPitch / encVolley, 0.0F, 5.0F) : 0;

                    BowItem bowItem = bow.getItem() instanceof BowItem ? (BowItem) bow.getItem() : null;

                    for (int shot = 0; shot < numArrows; ++shot) {
                        AbstractArrowEntity arrow = createArrow(world, ammo, shooter);
                        if (bowItem != null) {
                            arrow = bowItem.customArrow(arrow);
                        }
                        arrow.func_234612_a_(shooter, shooter.rotationPitch - volleyPitch * shot, shooter.rotationYaw, 0.0F, arrowVelocity * 3.0F * velocityMod, accuracyMod);// * (1 + shot * 2));
                        arrow.setDamage(arrow.getDamage() * damageMod);

                        if (arrowVelocity >= 1.0F) {
                            arrow.setIsCritical(true);
                        }
                        if (encTrueshot > 0) {
                            arrow.setPierceLevel((byte) encTrueshot);
                        }
                        if (encPower > 0 && arrow.getDamage() > 0) {
                            arrow.setDamage(arrow.getDamage() + (double) encPower * 0.5D + 0.5D);
                        }
                        if (encPunch > 0) {
                            arrow.setKnockbackStrength(encPunch);
                        }
                        if (encFlame > 0) {
                            arrow.setFire(100);
                        }
                        if (infinite || shot > 0) {
                            arrow.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        }
                        world.addEntity(arrow);
                    }
                    bowCap.onArrowLoosed(shooter);
                }
                world.playSound(null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                if (!infinite && !shooter.abilities.isCreativeMode) {
                    ammoCap.onArrowLoosed(shooter);
                    if (ammo.isEmpty()) {
                        shooter.inventory.deleteStack(ammo);
                    }
                }
                shooter.addStat(Stats.ITEM_USED.get(bow.getItem()));
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

    public static ItemStack findAmmo(PlayerEntity shooter) {

        ItemStack offHand = shooter.getHeldItemOffhand();
        ItemStack mainHand = shooter.getHeldItemMainhand();

        if (offHand.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isArrow(offHand)) {
            return offHand;
        }
        if (mainHand.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isArrow(mainHand)) {
            return mainHand;
        }
        for (ItemStack slot : shooter.inventory.mainInventory) {
            if (slot.getCapability(AMMO_ITEM_CAPABILITY).map(cap -> !cap.isEmpty(shooter)).orElse(false) || isArrow(slot)) {
                return slot;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Called specifically when looking for ARROWS in inventory.
     */
    public static ItemStack findArrows(PlayerEntity shooter) {

        ItemStack offHand = shooter.getHeldItemOffhand();
        ItemStack mainHand = shooter.getHeldItemMainhand();

        if (isSimpleArrow(offHand)) {
            return offHand;
        } else if (isSimpleArrow(mainHand)) {
            return mainHand;
        }
        for (int i = 0; i < shooter.inventory.getSizeInventory(); ++i) {
            ItemStack stack = shooter.inventory.getStackInSlot(i);
            if (isSimpleArrow(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

}
