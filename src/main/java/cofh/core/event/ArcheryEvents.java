package cofh.core.event;

import cofh.core.capability.templates.ArcheryBowItemWrapper;
import cofh.lib.util.constants.ModIds;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.core.capability.CapabilityArchery.BOW_ITEM_CAPABILITY;
import static cofh.core.util.helpers.ArcheryHelper.findAmmo;
import static cofh.core.util.helpers.ArcheryHelper.validBow;
import static cofh.core.util.references.EnsorcIDs.ID_QUICK_DRAW;
import static cofh.core.util.references.EnsorcIDs.ID_VOLLEY;
import static cofh.lib.util.Constants.DAMAGE_ARROW;
import static cofh.lib.util.Utils.*;
import static cofh.lib.util.constants.ModIds.ID_ENSORCELLATION;
import static net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS;

@Mod.EventBusSubscriber (modid = ModIds.ID_COFH_CORE)
public class ArcheryEvents {

    private ArcheryEvents() {

    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void handleArrowLooseEvent(ArrowLooseEvent event) {

        ItemStack bow = event.getBow();
        if (!validBow(bow)) {
            return;
        }
        Player shooter = event.getEntity();
        event.setCanceled(bow.getCapability(BOW_ITEM_CAPABILITY).orElse(new ArcheryBowItemWrapper(bow)).fireArrow(findAmmo(shooter, bow), shooter, event.getCharge(), event.getLevel()));
    }

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public static void handleArrowNockEvent(ArrowNockEvent event) {

        ItemStack bow = event.getBow();
        if (!validBow(bow)) {
            return;
        }
        Player shooter = event.getEntity();
        ItemStack ammo = findAmmo(shooter, bow);

        if (ammo.isEmpty() && getItemEnchantmentLevel(INFINITY_ARROWS, bow) > 0) {
            ammo = new ItemStack(Items.ARROW);
        }
        if (!ammo.isEmpty()) {
            shooter.startUsingItem(event.getHand());
            event.setAction(InteractionResultHolder.consume(bow));
        } else if (!shooter.getAbilities().instabuild) {
            event.setAction(InteractionResultHolder.fail(bow));
        }
    }

    @SubscribeEvent
    public static void handleItemUseTickEvent(LivingEntityUseItemEvent.Tick event) {

        int encQuickDraw = getItemEnchantmentLevel(getEnchantment(ID_ENSORCELLATION, ID_QUICK_DRAW), event.getItem());
        if (encQuickDraw > 0 && event.getDuration() > event.getItem().getUseDuration() - 20) {
            event.setDuration(event.getDuration() - encQuickDraw);
        }
    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleLivingHurtEvent(LivingHurtEvent event) {

        if (event.isCanceled()) {
            return;
        }
        Entity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof LivingEntity)) {
            return;
        }
        if (source.msgId.equals(DAMAGE_ARROW)) {
            int encVolley = getHeldEnchantmentLevel((LivingEntity) attacker, getEnchantment(ID_ENSORCELLATION, ID_VOLLEY));
            if (encVolley > 0) {
                entity.invulnerableTime = 0;
            }
        }
    }

}
