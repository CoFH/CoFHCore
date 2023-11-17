package cofh.core.item;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Objects;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.minecraft.world.InteractionHand.OFF_HAND;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ItemTracker {

    // TODO weak reference players
    protected static Map<Hand, ItemStack> HELD = new Object2ObjectOpenHashMap<>();
    protected static Object2IntMap<ItemStack> USING = new Object2IntOpenHashMap<>();

    //protected static Object2LongMap<ItemStack> TIME = new Object2LongOpenHashMap<>();
    static {
        USING.defaultReturnValue(-1);
        //TIME.defaultReturnValue(-1);
    }

    /**
     * Gets the duration for which the given item has been used.
     */
    public static int getUsingDuration(ItemStack stack) {

        return USING.getInt(stack);
    }

    ///**
    // * Stores a time value associated with the item until it is swapped off of.
    // */
    //public static void recordTime(ItemStack stack, long time) {
    //
    //    TIME.computeLongIfPresent(stack, (key, old) -> time);
    //}

    ///**
    // * Recalls the previously recorded time value, or -1 if not present.
    // */
    //public static long getRecordedTime(ItemStack stack) {
    //
    //    return TIME.getLong(stack);
    //}

    //@Nullable
    //public static Pair<Player, InteractionHand> getUser(ItemStack stack) {
    //
    //    if (stack.getItem() instanceof ITrackedItem tracked) {
    //        return HELD.entrySet().stream()
    //                .filter(entry -> tracked.matches(stack, entry.getValue()))
    //                .findFirst()
    //                .map(entry -> {
    //                    Hand hand = entry.getKey();
    //                    if (hand == null) {
    //                        return null;
    //                    }
    //                    return Pair.of(hand.player, hand.hand);
    //                })
    //                .orElse(null);
    //    }
    //    return null;
    //}

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void playerTick(TickEvent.PlayerTickEvent event) {

        Player player = event.player;
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        updateData(player, MAIN_HAND);
        updateData(player, OFF_HAND);
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onStartUsing(LivingEntityUseItemEvent.Start event) {

        if (!event.isCanceled()) {
            updateUsing(event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onTickUsing(LivingEntityUseItemEvent.Tick event) {

        if (!event.isCanceled()) {
            updateUsing(event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent
    public static void onEndUsing(LivingEntityUseItemEvent.Stop event) {

        stopUsing(event.getItem());
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {

        stopUsing(event.getItem());
    }

    // region HELPERS
    protected static void updateData(Player player, InteractionHand hand) {

        Hand key = new Hand(player, hand);
        ItemStack previous = HELD.remove(key);
        int duration = USING.removeInt(previous);
        //long time = TIME.removeLong(previous);
        ItemStack current = player.getItemInHand(hand);
        if (previous != null && previous.getItem() instanceof ITrackedItem item) {
            if (item.matches(previous, current)) {
                HELD.put(key, current);
                USING.put(current, duration);
                //TIME.put(current, time);
                return;
            }
            item.onSwapFrom(player, hand, previous, current, duration);
        }
        if (current.getItem() instanceof ITrackedItem item) {
            HELD.put(key, current);
            USING.put(current, duration);
            //TIME.put(current, time);
            item.onSwapTo(player, hand, previous, current);
        }
    }

    protected static void updateUsing(ItemStack stack, int duration) {

        USING.computeIntIfPresent(stack, (key, old) -> stack.getUseDuration() - duration);
    }

    protected static void stopUsing(ItemStack stack) {

        USING.computeIntIfPresent(stack, (key, old) -> -1);
    }
    // endregion

    protected record Hand(Player player, InteractionHand hand) {

        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }
            if (o instanceof Hand other) {
                return player.equals(other.player) && player.isLocalPlayer() == other.player.isLocalPlayer() && hand.equals(other.hand);
            }
            return false;
        }

        @Override
        public int hashCode() {

            return Objects.hash(player.isLocalPlayer(), player, hand);
        }

    }

}
