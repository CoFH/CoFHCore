package cofh.core.event;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreReferences.CHILLED;
import static cofh.lib.util.references.CoreReferences.SHOCKED;
import static net.minecraft.potion.Effects.POISON;
import static net.minecraft.potion.Effects.WITHER;

@Mod.EventBusSubscriber(modid = ID_COFH_CORE)
public class ArmorEvents {

    private ArmorEvents() {

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();
        float amount = event.getAmount();

        double hazRes = getHazardResistance(entity);
        if (hazRes > 0.0D) {
            if (source.isFireDamage() || HAZARD_DAMAGE_TYPES.contains(source.getDamageType())) {
                if (entity.getRNG().nextDouble() < hazRes) {
                    entity.extinguish();
                    attemptDamagePlayerArmor(entity, amount);
                    event.setCanceled(true);
                }
            }
        }
        double stingRes = getStingResistance(entity);
        if (stingRes > 0.0D) {
            if (STING_DAMAGE_TYPES.contains(source.getDamageType())) {
                if (entity.getRNG().nextDouble() < stingRes) {
                    attemptDamagePlayerArmor(entity, amount);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handleLivingFallEvent(LivingFallEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();

        double fallRes = getFallResistance(entity);
        if (fallRes != 0.0D) {
            event.setDistance(Math.max(0, event.getDistance() - (float) fallRes));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void handlePotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        EffectInstance effect = event.getPotionEffect();

        double hazRes = getHazardResistance(entity);
        if (hazRes > 0.0D) {
            if (HAZARD_EFFECTS.contains(effect.getPotion())) {
                if (entity.getRNG().nextDouble() < hazRes) {
                    attemptDamagePlayerArmor(entity, (1 + effect.getAmplifier()) * effect.getDuration() / 40F);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    // TODO: Is this necessary? Applicable should cover it.
    //    @SubscribeEvent(priority = EventPriority.HIGH)
    //    public static void handlePotionAddedEvent(PotionEvent.PotionAddedEvent event) {
    //
    //        if (event.isCanceled()) {
    //            return;
    //        }
    //        LivingEntity entity = event.getEntityLiving();
    //        EffectInstance effect = event.getPotionEffect();
    //
    //        double hazRes = entity.getAttribute(CoreAttributes.HAZARD_RESISTANCE).getValue();
    //        if (hazRes > 0.0D) {
    //            if (HAZARD_EFFECTS.contains(effect.getPotion())) {
    //                if (entity.getRNG().nextDouble() < hazRes) {
    //                }
    //            }
    //        }
    //    }

    // TODO: Adjust?
    // region HELPERS
    private static void attemptDamagePlayerArmor(Entity entity, float amount) {

        if (entity instanceof PlayerEntity) {
            if (100 * entity.world.rand.nextFloat() < amount) {
                ((PlayerEntity) entity).inventory.func_234563_a_(DamageSource.GENERIC, Math.min(20.0F, amount));
            }
        }
    }

    private static double getFallResistance(Entity entity) {

        double ret = 0.0D;
        for (ItemStack armor : entity.getArmorInventoryList()) {
            ret += FALL_RESISTANCE_MAP.getOrDefault(armor.getItem(), 0.0D);
        }
        return ret;
    }

    private static double getHazardResistance(Entity entity) {

        double ret = 0.0D;
        for (ItemStack armor : entity.getArmorInventoryList()) {
            ret += HAZARD_RESISTANCE_MAP.getOrDefault(armor.getItem(), 0.0D);
        }
        return ret;
    }

    private static double getStingResistance(Entity entity) {

        double ret = 0.0D;
        for (ItemStack armor : entity.getArmorInventoryList()) {
            ret += STING_RESISTANCE_MAP.getOrDefault(armor.getItem(), 0.0D);
        }
        return ret;
    }
    // endregion

    public static void registerFallResistArmor(Item armor, double resistance) {

        FALL_RESISTANCE_MAP.put(armor, resistance);
    }

    public static void registerHazardResistArmor(Item armor, double resistance) {

        HAZARD_RESISTANCE_MAP.put(armor, resistance);
    }

    public static void registerStingResistArmor(Item armor, double resistance) {

        STING_RESISTANCE_MAP.put(armor, resistance);
    }

    private static final Object2ObjectOpenHashMap<Item, Double> FALL_RESISTANCE_MAP = new Object2ObjectOpenHashMap<>();

    private static final Object2ObjectOpenHashMap<Item, Double> HAZARD_RESISTANCE_MAP = new Object2ObjectOpenHashMap<>();
    private static final Set<String> HAZARD_DAMAGE_TYPES = new ObjectOpenHashSet<>();
    private static final Set<Effect> HAZARD_EFFECTS = new ObjectOpenHashSet<>();

    private static final Object2ObjectOpenHashMap<Item, Double> STING_RESISTANCE_MAP = new Object2ObjectOpenHashMap<>();
    private static final Set<String> STING_DAMAGE_TYPES = new ObjectOpenHashSet<>();

    public static void setup() {

        STING_DAMAGE_TYPES.add("sting");
        STING_DAMAGE_TYPES.add("cactus");
        STING_DAMAGE_TYPES.add("sweetBerryBush");

        HAZARD_DAMAGE_TYPES.add("lightningBolt");
        HAZARD_DAMAGE_TYPES.add("cold");
        HAZARD_DAMAGE_TYPES.add("lightning");

        HAZARD_EFFECTS.add(POISON);
        HAZARD_EFFECTS.add(WITHER);

        HAZARD_EFFECTS.add(CHILLED);
        HAZARD_EFFECTS.add(SHOCKED);
    }

}
