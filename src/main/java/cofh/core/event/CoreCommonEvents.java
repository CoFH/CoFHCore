package cofh.core.event;

import cofh.core.config.CoreCommonConfig;
import cofh.core.config.CoreEnchantConfig;
import cofh.core.util.helpers.XpHelper;
import cofh.lib.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

import static cofh.core.init.CoreMobEffects.SLIMED;
import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.Utils.getMaxEquippedEnchantmentLevel;
import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.world.item.enchantment.Enchantments.FALL_PROTECTION;
import static net.minecraft.world.item.enchantment.Enchantments.MENDING;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class CoreCommonEvents {

    private CoreCommonEvents() {

    }

    @SubscribeEvent
    public static void handleFarmlandTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {

        if (event.isCanceled()) {
            return;
        }
        if (!CoreEnchantConfig.improvedFeatherFalling()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            int encFeatherFalling = getMaxEquippedEnchantmentLevel((LivingEntity) entity, FALL_PROTECTION);
            if (encFeatherFalling > 0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void handleLivingFallEvent(LivingFallEvent event) {

        if (event.isCanceled()) {
            return;
        }
        if (event.getDistance() >= 3.0) {
            LivingEntity living = event.getEntity();
            if (living.hasEffect(SLIMED.get())) {
                Vec3 motion = living.getDeltaMovement();
                living.setDeltaMovement(motion.x, 0.08 * Math.sqrt(event.getDistance() / 0.08), motion.z);
                living.hurtMarked = true;
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handleItemFishedEvent(ItemFishedEvent event) {

        if (event.isCanceled()) {
            return;
        }
        if (!CoreCommonConfig.enableFishingExhaustion()) {
            return;
        }
        Entity player = event.getHookEntity().getOwner();
        if (!(player instanceof Player) || player instanceof FakePlayer) {
            return;
        }
        ((Player) player).causeFoodExhaustion(CoreCommonConfig.amountFishingExhaustion());
    }

    @SubscribeEvent (priority = EventPriority.LOW)
    public static void handlePickupXpEvent(PlayerXpEvent.PickupXp event) {

        if (event.isCanceled()) {
            return;
        }
        Player player = event.getEntity();
        ExperienceOrb orb = event.getOrb();

        // Improved Mending
        if (CoreEnchantConfig.improvedMending()) {
            player.takeXpDelay = 2;
            player.take(orb, 1);

            Map.Entry<EquipmentSlot, ItemStack> entry = getMostDamagedItem(player);
            if (entry != null) {
                ItemStack itemstack = entry.getValue();
                if (!itemstack.isEmpty() && itemstack.isDamaged()) {
                    int i = Math.min((int) (orb.value * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
                    orb.value -= durabilityToXp(i);
                    itemstack.setDamageValue(itemstack.getDamageValue() - i);
                }
            }
            XpHelper.attemptStoreXP(player, orb);
            if (orb.value > 0) {
                player.giveExperiencePoints(orb.value);
            }
            orb.discard();
            event.setCanceled(true);
            return;
        }
        XpHelper.attemptStoreXP(player, orb);
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void handleSaplingGrowTreeEvent(SaplingGrowTreeEvent event) {

        if (event.isCanceled()) {
            return;
        }
        if (!CoreCommonConfig.enableSaplingGrowthMod()) {
            return;
        }
        if (event.getRandomSource().nextInt(CoreCommonConfig.amountSaplingGrowthMod()) != 0) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {

        if (event.phase == TickEvent.Phase.START) {
            Utils.tickTimeConstants();
        }
    }

    // region HELPERS
    private static Map.Entry<EquipmentSlot, ItemStack> getMostDamagedItem(Player player) {

        Map<EquipmentSlot, ItemStack> map = MENDING.getSlotItems(player);
        Map.Entry<EquipmentSlot, ItemStack> mostDamaged = null;
        if (map.isEmpty()) {
            return null;
        }
        double durability = 0.0D;

        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack stack = entry.getValue();
            if (!stack.isEmpty() && getItemEnchantmentLevel(MENDING, stack) > 0) {
                if (calcDurabilityRatio(stack) > durability) {
                    mostDamaged = entry;
                    durability = calcDurabilityRatio(stack);
                }
            }
        }
        return mostDamaged;
    }

    private static int durabilityToXp(int durability) {

        return durability / 2;
    }

    private static int xpToDurability(int xp) {

        return xp * 2;
    }

    private static double calcDurabilityRatio(ItemStack stack) {

        return (double) stack.getDamageValue() / stack.getMaxDamage();
    }
    // endregion
}
