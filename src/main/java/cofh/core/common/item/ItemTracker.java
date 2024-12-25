package cofh.core.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.minecraft.world.InteractionHand.OFF_HAND;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ItemTracker {

    // TODO weak reference players
    protected static Map<Hand, Held> HELD = new HashMap<>();

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

        if (!event.isCanceled() && event.getEntity() instanceof Player player) {
            updateUsing(player, event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent (priority = EventPriority.LOWEST)
    public static void onTickUsing(LivingEntityUseItemEvent.Tick event) {

        if (!event.isCanceled() && event.getEntity() instanceof Player player) {
            updateUsing(player, event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent
    public static void onEndUsing(LivingEntityUseItemEvent.Stop event) {

        if (event.getEntity() instanceof Player player) {
            stopUsing(player, event.getItem());
        }
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {

        if (event.getEntity() instanceof Player player) {
            stopUsing(player, event.getItem());
        }
    }

    // region HELPERS
    protected static void updateData(Player player, InteractionHand hand) {

        Hand key = new Hand(player, hand);
        Held held = HELD.get(key);
        ItemStack current = player.getItemInHand(hand);
        if (held != null && held.stack.getItem() instanceof ITrackedItem item) {
            if (item.matches(held.stack, current)) {
                return;
            }
            HELD.remove(key);
            item.onSwapFrom(player, hand, held.stack, current, held.duration);
        }
        if (current.getItem() instanceof ITrackedItem item) {
            ItemStack copy = current.copy();
            HELD.put(key, new Held(copy));
            item.onSwapTo(player, hand, held == null ? null : held.stack, current);
        }
    }

    protected static void updateUsing(Player player, ItemStack stack, int duration) {

        setUseDuration(player, stack, stack.getUseDuration() - duration);
    }

    protected static void stopUsing(Player player, ItemStack stack) {

        setUseDuration(player, stack, -1);
    }

    protected static void setUseDuration(Player player, ItemStack stack, int duration) {

        Held held = HELD.get(new Hand(player, player.getUsedItemHand()));
        if (held != null) {
            held.duration = duration;
        }
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

    protected static class Held {

        protected ItemStack stack;
        protected int duration = -1;

        protected Held(ItemStack stack) {

            this.stack = stack;
        }

    }

}
