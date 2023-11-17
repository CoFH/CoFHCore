//package cofh.lib.api.item;
//
//import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.EntityHitResult;
//import net.minecraft.world.phys.HitResult;
//import net.minecraft.world.phys.Vec3;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//import net.minecraftforge.eventbus.api.EventPriority;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import javax.annotation.Nullable;
//import java.util.Map;
//
//import static cofh.lib.util.constants.ModIds.ID_COFH_CORE;
//import static net.minecraft.world.InteractionHand.MAIN_HAND;
//import static net.minecraft.world.InteractionHand.OFF_HAND;
//
///**
// * Marker interface for any items that should be tracked.
// */
//@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
//public interface ITrackedItem {
//
//    default void onSelect(Player player, InteractionHand hand, TrackedItemInfo info) {
//
//    }
//
//    /**
//     * Note that the itemstack may not still be in the player's inventory, so this method should not be used for critical alterations to the itemstack.
//     */
//    default void onDeselect(Player player, InteractionHand hand, TrackedItemInfo info) {
//
//    }
//
//    default void onSwapHand(Player player, InteractionHand swappedTo, TrackedItemInfo info) {
//
//    }
//
//    // region Item Tracking
//    Map<Player, TrackedItemInfo> mainHand = new Object2ObjectOpenHashMap<>();
//    Map<Player, TrackedItemInfo> offHand = new Object2ObjectOpenHashMap<>();
//
//    @SubscribeEvent(priority = EventPriority.HIGHEST)
//    static void handlePlayerTickEvent(TickEvent.PlayerTickEvent event) {
//
//        if (event.isCanceled()) {
//            return;
//        }
//        Player player = event.player;
//        long time = player.level.getGameTime();
//
//        TrackedItemInfo oldMain = mainHand.get(player);
//        TrackedItemInfo oldOff = offHand.get(player);
//        ItemStack newMain = player.getMainHandItem();
//        ItemStack newOff = player.getOffhandItem();
//        if (oldMain != null && oldMain.stack.getItem() instanceof ITrackedItem item) {
//            if (!oldMain.stack.equals(newMain)) { // TODO itemstack equality
//                if (oldMain.stack.equals(newOff)) {
//                    offHand.put(player, oldMain);
//                    item.onSwapHand(player, OFF_HAND, oldMain);
//                } else {
//                    item.onDeselect(player, MAIN_HAND, oldMain);
//                }
//                mainHand.remove(player);
//            }
//        } else if (newMain.getItem() instanceof ITrackedItem item) {
//            TrackedItemInfo info = new TrackedItemInfo(newMain, player);
//            mainHand.put(player, info);
//            item.onSelect(player, MAIN_HAND, info);
//        } else {
//            mainHand.remove(player);
//        }
//
//        if (oldOff != null && oldOff.stack.getItem() instanceof ITrackedItem item) {
//            if (!oldOff.stack.equals(newOff)) {
//                if (oldOff.stack.equals(newMain)) {
//                    mainHand.put(player, oldOff);
//                    item.onSwapHand(player, MAIN_HAND, oldOff);
//                } else {
//                    item.onDeselect(player, OFF_HAND, oldOff);
//                }
//                offHand.remove(player);
//            }
//        } else if (newOff.getItem() instanceof ITrackedItem item) {
//            TrackedItemInfo info = new TrackedItemInfo(newOff, player);
//            offHand.put(player, info);
//            item.onSelect(player, OFF_HAND, info);
//        } else {
//            offHand.remove(player);
//        }
//    }
//
//    static TrackedItemInfo getInfo(Player player, InteractionHand hand) {
//
//        if (hand == MAIN_HAND) {
//            return mainHand.get(player);
//        }
//        return offHand.get(player);
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    static void handleRightClickItemEvent(PlayerInteractEvent.RightClickItem event) {
//
//        if (event.isCanceled()) {
//            return;
//        }
//        Player player = event.getPlayer();
//        if (event.getItemStack().getItem() instanceof ITrackedItem) {
//            getInfo(player, event.getHand()).startUse = new TrackedItemContext(player);
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    static void handleRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
//
//        if (event.isCanceled()) {
//            return;
//        }
//        Player player = event.getPlayer();
//        if (event.getItemStack().getItem() instanceof ITrackedItem) {
//            getInfo(player, event.getHand()).startUse = new TrackedItemContext(player, event.getHitVec());
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    static void handleInteractEntityEvent(PlayerInteractEvent.EntityInteractSpecific event) {
//
//        if (event.isCanceled()) {
//            return;
//        }
//        Player player = event.getPlayer();
//        if (event.getItemStack().getItem() instanceof ITrackedItem) {
//            Entity target = event.getEntity();
//            getInfo(player, event.getHand()).startUse = new TrackedItemContext(player, new EntityHitResult(target, event.getLocalPos().add(target.position())));
//        }
//    }
//    // endregion
//
//    class TrackedItemInfo {
//
//        public final ItemStack stack;
//        public final TrackedItemContext onSelect;
//        @Nullable
//        public TrackedItemContext startUse;
//        @Nullable
//        public TrackedItemContext endUse;
//
//        public TrackedItemInfo(ItemStack stack, Player player) {
//
//            this.stack = stack;
//            this.onSelect = new TrackedItemContext(player);
//        }
//
//    }
//
//    record TrackedItemContext(Level level, long time, Vec3 eyePos, float xRot, float yRot, HitResult hit) {
//
//        static HitResult MISS = new HitResult(Vec3.ZERO) {
//
//            @Override
//            public Type getType() {
//                return Type.MISS;
//            }
//        };
//
//        public TrackedItemContext(Player player) {
//
//            this(player, MISS);
//        }
//
//        public TrackedItemContext(Player player, HitResult hit) {
//
//            this(player.level, player.level.getGameTime(), player.getEyePosition(), player.xRot, player.yRot, hit);
//        }
//
//    }
//
//}
