package cofh.lib.util;

import cofh.core.item.ITrackedItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.world.InteractionHand.*;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ItemTracker {

    protected static Map<Player, TrackedItemData> MAIN_HAND_DATA = new Object2ObjectOpenHashMap<>();
    protected static Map<Player, TrackedItemData> OFF_HAND_DATA = new Object2ObjectOpenHashMap<>();

    public static Map<Player, TrackedItemData> getMap(InteractionHand hand) {

        return hand == OFF_HAND ? OFF_HAND_DATA : MAIN_HAND_DATA;
    }

    public static TrackedItemData getData(Player player, InteractionHand hand) {

        return ItemTracker.getMap(hand).get(player);
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void playerTick(TickEvent.PlayerTickEvent event) {

        Player player = event.player;
        if (event.phase != TickEvent.Phase.START || player.level.isClientSide) {
            return;
        }
        updateData(player, MAIN_HAND);
        updateData(player, OFF_HAND);
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onUse(PlayerInteractEvent.RightClickItem event) {

        if (!event.isCanceled() && event.getCancellationResult() == InteractionResult.SUCCESS && !event.getLevel().isClientSide) {
            itemUsed(event.getEntity(), event.getHand(), event.getItemStack());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onUseOn(PlayerInteractEvent.RightClickBlock event) {

        if (!event.isCanceled() && event.getCancellationResult() == InteractionResult.SUCCESS && !event.getLevel().isClientSide) {
            itemUsed(event.getEntity(), event.getHand(), event.getItemStack());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onUseEntity(PlayerInteractEvent.EntityInteract event) {

        if (!event.isCanceled() && event.getCancellationResult() == InteractionResult.SUCCESS && !event.getLevel().isClientSide) {
            itemUsed(event.getEntity(), event.getHand(), event.getItemStack());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onStartUsing(LivingEntityUseItemEvent.Start event) {

        if (!event.isCanceled() && event.getEntity() instanceof Player player && !player.level.isClientSide) {
            itemUsed(player, player.getUsedItemHand(), event.getItem());
            itemUsing(player, player.getUsedItemHand(), event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onTickUsing(LivingEntityUseItemEvent.Tick event) {

        if (!event.isCanceled() && event.getEntity() instanceof Player player && !player.level.isClientSide) {
            itemUsing(player, player.getUsedItemHand(), event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent
    public static void onEndUsing(LivingEntityUseItemEvent.Stop event) {

        if (event.getEntity() instanceof Player player && !player.level.isClientSide) {
            itemUsing(player, player.getUsedItemHand(), event.getItem(), -1);
        }
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {

        if (event.getEntity() instanceof Player player && !player.level.isClientSide) {
            itemUsing(player, player.getUsedItemHand(), event.getItem(), -1);
        }
    }

    // region HELPERS
    protected static void updateData(Player player, InteractionHand hand) {

        Map<Player, TrackedItemData> map = getMap(hand);
        TrackedItemData data = map.get(player);
        ItemStack current = player.getItemInHand(hand);
        if (data == null) {
            if (current.getItem() instanceof ITrackedItem) {
                swapTo(player, hand, current);
            } else {
                map.remove(player);
            }
        } else if (!(data.matches(current))) {
            ((ITrackedItem) data.stack.getItem()).onSwapFrom(player, hand, data.stack, data.duration);
            if (current.getItem() instanceof ITrackedItem) {
                swapTo(player, hand, current);
            } else {
                map.remove(player);
            }
        }
    }

    protected static void swapTo(Player player, InteractionHand hand, ItemStack stack) {

        getMap(hand).put(player, new TrackedItemData(stack));
        ((ITrackedItem) stack.getItem()).onSwapTo(player, hand, stack);
    }

    protected static void itemUsed(Player player, InteractionHand hand, ItemStack stack) {

        if (stack.getItem() instanceof ITrackedItem) {
            Map<Player, TrackedItemData> map = getMap(hand);
            TrackedItemData data = map.get(player);
            if (data == null || !data.matches(stack)) {
                data = new TrackedItemData(stack);
                map.put(player, data);
            }
            data.lastUse = player.level.getGameTime();
        }
    }

    protected static void itemUsing(Player player, InteractionHand hand, ItemStack stack, int duration) {

        if (stack.getItem() instanceof ITrackedItem) {
            Map<Player, TrackedItemData> map = getMap(hand);
            TrackedItemData data = map.get(player);
            if (data == null || !data.matches(stack)) {
                data = new TrackedItemData(stack);
                map.put(player, data);
            }
            if (duration < 0) {
                data.duration = duration;
            } else {
                data.duration = stack.getUseDuration() - duration;
            }
        }
    }
    // endregion

    public static class TrackedItemData {

        public ItemStack stack;

        /**
         * The last time the item was starting to be used, i.e. {@link net.minecraft.world.item.Item#use(Level, Player, InteractionHand)}.
         * -1 if the item has not yet been used after swapping to it.
         */
        public long lastUse = -1;

        /**
         * The number of ticks the item has been used for. -1 if the item is not currently being used.
         */
        public int duration = -1;

        public TrackedItemData(ItemStack stack) {

            this.stack = stack;
        }

        public boolean matches(ItemStack to) {

            return ((ITrackedItem) stack.getItem()).matches(stack, to);
        }

    }

}
