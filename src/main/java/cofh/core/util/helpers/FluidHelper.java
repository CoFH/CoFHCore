package cofh.core.util.helpers;

import cofh.lib.api.item.IFluidContainerItem;
import cofh.lib.common.fluid.FluidStorageCoFH;
import cofh.lib.init.tags.FluidTagsCoFH;
import cofh.lib.util.helpers.BlockHelper;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static cofh.lib.util.Constants.BOTTLE_VOLUME;
import static cofh.lib.util.Constants.BUCKET_VOLUME;
import static cofh.lib.util.constants.NBTTags.TAG_POTION;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public final class FluidHelper {

    private FluidHelper() {

    }

    public static final Predicate<FluidStack> IS_WATER = e -> e.getFluid().equals(Fluids.WATER);
    public static final Predicate<FluidStack> IS_LAVA = e -> e.getFluid().equals(Fluids.LAVA);
    public static final Predicate<FluidStack> IS_XP = e -> e.getFluid().is(FluidTagsCoFH.EXPERIENCE);

    public static final Map<Item, Function<ItemStack, FluidStack>> BOTTLE_DRAIN_MAP = new Object2ObjectOpenHashMap<>();
    public static final Map<Predicate<FluidStack>, Function<FluidStack, ItemStack>> BOTTLE_FILL_MAP = new Object2ObjectOpenHashMap<>();

    public static int fluidHashcodeNoTag(FluidStack stack) {

        return stack.getFluid().hashCode();
    }

    public static int fluidHashcode(FluidStack stack) {

        return stack.getTag() != null ? stack.getFluid().hashCode() + 31 * stack.getTag().hashCode() : stack.getFluid().hashCode();
    }

    // region COMPARISON
    public static boolean fluidsEqualWithTags(FluidStack resourceA, FluidStack resourceB) {

        return fluidsEqual(resourceA, resourceB) && FluidStack.areFluidStackTagsEqual(resourceA, resourceB);
    }

    public static boolean fluidsEqual(FluidStack resourceA, FluidStack resourceB) {

        return resourceA != null && resourceA.isFluidEqual(resourceB) || resourceA == null && resourceB == null;
    }

    public static boolean fluidsEqual(Fluid fluidA, FluidStack resourceB) {

        return fluidA != null && resourceB != null && fluidA == resourceB.getFluid();
    }

    public static boolean fluidsEqual(FluidStack resourceA, Fluid fluidB) {

        return fluidB != null && resourceA != null && fluidB == resourceA.getFluid();
    }

    public static boolean fluidsEqual(Fluid fluidA, Fluid fluidB) {

        return fluidA != null && fluidA.equals(fluidB);
    }
    // endregion

    // region BLOCK HELPERS
    public static boolean isWater(BlockState state) {

        return state.getBlock() == Blocks.WATER;
    }
    // endregion

    // region BLOCK TRANSFER
    public static boolean extractFromAdjacent(BlockEntity tile, FluidStorageCoFH tank, int amount, Direction side) {

        amount = Math.min(amount, tank.getSpace());
        if (!tank.getFluidStack().isEmpty()) {
            return extractFromAdjacent(tile, tank, new FluidStack(tank.getFluidStack(), amount), side);
        }
        BlockEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
        Direction opposite = side.getOpposite();

        IFluidHandler handler = getFluidHandlerCap(adjTile, opposite);
        if (handler == EmptyFluidHandler.INSTANCE) {
            return false;
        }
        FluidStack drainStack = handler.drain(amount, SIMULATE);
        int drainAmount = tank.fill(drainStack, EXECUTE);
        if (drainAmount > 0) {
            handler.drain(drainAmount, EXECUTE);
            return true;
        }
        return false;
    }

    public static boolean extractFromAdjacent(BlockEntity tile, FluidStorageCoFH tank, FluidStack resource, Direction side) {

        BlockEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
        Direction opposite = side.getOpposite();

        IFluidHandler handler = getFluidHandlerCap(adjTile, opposite);
        if (handler == EmptyFluidHandler.INSTANCE) {
            return false;
        }
        FluidStack drainStack = handler.drain(resource, SIMULATE);
        int drainAmount = tank.fill(drainStack, EXECUTE);
        if (drainAmount > 0) {
            handler.drain(new FluidStack(resource, drainAmount), EXECUTE);
            return true;
        }
        return false;
    }

    public static boolean insertIntoAdjacent(BlockEntity tile, FluidStorageCoFH tank, int amount, Direction side) {

        if (tank.isEmpty()) {
            return false;
        }
        amount = Math.min(amount, tank.getAmount());

        BlockEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
        Direction opposite = side.getOpposite();

        IFluidHandler handler = getFluidHandlerCap(adjTile, opposite);
        if (handler == EmptyFluidHandler.INSTANCE) {
            return false;
        }
        int fillAmount = handler.fill(new FluidStack(tank.getFluidStack(), amount), EXECUTE);
        if (fillAmount > 0) {
            tank.drain(fillAmount, EXECUTE);
            return true;
        }
        return false;
    }

    public static boolean hasFluidHandlerCap(BlockEntity tile, Direction face) {

        return tile != null && tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).isPresent();
    }

    public static IFluidHandler getFluidHandlerCap(BlockEntity tile, Direction face) {

        return tile == null ? EmptyFluidHandler.INSTANCE : tile.getCapability(ForgeCapabilities.FLUID_HANDLER, face).orElse(EmptyFluidHandler.INSTANCE);
    }
    // endregion

    // region CAPABILITY HELPERS
    public static boolean hasFluidHandlerCap(ItemStack item) {

        return !item.isEmpty() && item.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandlerCap(@Nonnull ItemStack stack) {

        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
    }

    public static Optional<FluidStack> getFluidContainedInItem(@Nonnull ItemStack container) {

        if (!container.isEmpty()) {
            Optional<FluidStack> fluidContained = getFluidHandlerCap(container).map(c -> c.getFluidInTank(0));
            if (fluidContained.isPresent() && !fluidContained.get().isEmpty()) {
                return fluidContained;
            }
            if (container.getItem() instanceof IFluidContainerItem fluidContainerItem) {
                fluidContained = Optional.of(fluidContainerItem.getFluid(container));
            }
            if (fluidContained.isPresent() && !fluidContained.get().isEmpty()) {
                return fluidContained;
            }
        }
        return Optional.empty();
    }

    public static int getCapacityForItem(@Nonnull ItemStack container) {

        if (!container.isEmpty()) {
            return getFluidHandlerCap(container).map(c -> c.getTankCapacity(0)).orElse(0);
        }
        return 0;
    }

    /**
     * Attempts to drain the item to an IFluidHandler. These are special-cased.
     *
     * @param stack   The stack (bottle) to drain from.
     * @param handler The IFluidHandler to fill.
     * @param player  The player using the item.
     * @param hand    The hand the player is holding the item in.
     * @return If the interaction was successful.
     */
    public static boolean drainBottleToHandler(ItemStack stack, IFluidHandler handler, Player player, InteractionHand hand) {

        FluidStack fluid = BOTTLE_DRAIN_MAP.containsKey(stack.getItem()) ? BOTTLE_DRAIN_MAP.get(stack.getItem()).apply(stack) : FluidStack.EMPTY;
        return !fluid.isEmpty() && addEmptyBottleToPlayer(stack, fluid, handler, player, hand);
    }

    private static boolean addEmptyBottleToPlayer(ItemStack stack, FluidStack fluid, IFluidHandler handler, Player player, InteractionHand hand) {

        if (handler.fill(fluid, SIMULATE) == BOTTLE_VOLUME) {
            handler.fill(fluid, EXECUTE);
            if (!player.getAbilities().instabuild) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                player.setItemInHand(hand, ItemHelper.consumeItem(stack, 1));
                if (!player.addItem(bottle)) {
                    player.drop(bottle, false);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Attempts to fill the item from an IFluidHandler.
     *
     * @param stack   The stack to fill.
     * @param handler The IFluidHandler to drain from.
     * @param player  The player using the item.
     * @param hand    The hand the player is holding the item in.
     * @return If the interaction was successful.
     */
    public static boolean fillBottleFromHandler(ItemStack stack, IFluidHandler handler, Player player, InteractionHand hand) {

        if (stack.getItem() != Items.GLASS_BOTTLE) {
            return false;
        }
        FluidStack fluid = handler.drain(BOTTLE_VOLUME, SIMULATE);
        if (fluid.getAmount() != BOTTLE_VOLUME) {
            return false;
        }
        ItemStack bottle = ItemStack.EMPTY;

        for (Map.Entry<Predicate<FluidStack>, Function<FluidStack, ItemStack>> entry : BOTTLE_FILL_MAP.entrySet()) {
            if (entry.getKey().test(fluid)) {
                bottle = entry.getValue().apply(fluid);
                break;
            }
        }
        return !bottle.isEmpty() && addFilledBottleToPlayer(stack, bottle, handler, player, hand);
    }

    /**
     * This basically ensures that a given fluid HAS a bucket, if the ItemStack being used is a bucket.
     */
    public static boolean bucketFillTest(ItemStack stack, IFluidHandler handler) {

        if (stack.getItem() != Items.BUCKET) {
            return true;
        }
        FluidStack fluid = handler.drain(BUCKET_VOLUME, SIMULATE);
        if (fluid.getAmount() != BUCKET_VOLUME) {
            return false;
        }
        return fluid.getFluid().getBucket() != Items.AIR;
    }

    private static boolean addFilledBottleToPlayer(ItemStack stack, ItemStack bottle, IFluidHandler handler, Player player, InteractionHand hand) {

        if (handler.drain(BOTTLE_VOLUME, EXECUTE).getAmount() == BOTTLE_VOLUME) {
            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, ItemHelper.consumeItem(stack, 1));
                if (!player.addItem(bottle)) {
                    player.drop(bottle, false);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Attempts to drain the item to an IFluidHandler.
     *
     * @param stack   The stack to drain from.
     * @param handler The IFluidHandler to fill.
     * @param player  The player using the item.
     * @param hand    The hand the player is holding the item in.
     * @return If the interaction was successful.
     */
    public static boolean drainItemToHandler(ItemStack stack, IFluidHandler handler, Player player, InteractionHand hand) {

        if (stack.isEmpty() || handler == null || player == null) {
            return false;
        }
        if (drainBottleToHandler(stack, handler, player, hand)) {
            player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        IItemHandler playerInv = new InvWrapper(player.getInventory());
        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player, true);
        if (result.isSuccess()) {
            player.setItemInHand(hand, result.getResult());
            return true;
        }
        return false;
    }

    /**
     * Attempts to fill the item from an IFluidHandler.
     *
     * @param stack   The stack to fill.
     * @param handler The IFluidHandler to drain from.
     * @param player  The player using the item.
     * @param hand    The hand the player is holding the item in.
     * @return If the interaction was successful.
     */
    public static boolean fillItemFromHandler(ItemStack stack, IFluidHandler handler, Player player, InteractionHand hand) {

        if (stack.isEmpty() || handler == null || player == null || !bucketFillTest(stack, handler)) {
            return false;
        }
        if (fillBottleFromHandler(stack, handler, player, hand)) {
            player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (stack.getCount() == 1) {
            FluidStack containedFluid = getFluidContainedInItem(stack).orElse(FluidStack.EMPTY);
            int tankSpace = getCapacityForItem(stack) - containedFluid.getAmount();
            if (!containedFluid.isEmpty() && tankSpace > 0) {
                stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(e -> {
                    if (player.getAbilities().instabuild) {
                        handler.drain(new FluidStack(containedFluid, tankSpace), EXECUTE);
                    } else {
                        FluidUtil.tryFluidTransfer(e, handler, new FluidStack(containedFluid, tankSpace), true);
                    }
                });
                return true;
            }
        }
        IItemHandler playerInv = new InvWrapper(player.getInventory());
        FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player, true);
        if (result.isSuccess()) {
            player.setItemInHand(hand, result.getResult());
            return true;
        }
        return false;
    }

    /**
     * Attempts to interact the item with an IFluidHandler.
     * Interaction will always try and drain the item first, if this fails it will attempt to fill the item.
     *
     * @param stack   The stack to interact with.
     * @param handler The Handler to fill / drain.
     * @param player  The player using the item.
     * @param hand    The hand the player is holding the item in.
     * @return If any interaction with the handler was successful.
     */
    public static boolean interactWithHandler(ItemStack stack, IFluidHandler handler, Player player, InteractionHand hand) {

        return drainItemToHandler(stack, handler, player, hand) || fillItemFromHandler(stack, handler, player, hand);
    }
    // endregion

    // region POTION HELPERS
    public static boolean hasPotionTag(FluidStack stack) {

        return !stack.isEmpty() && stack.getTag() != null && stack.getTag().contains(TAG_POTION);
    }

    public static Potion getPotionFromFluid(FluidStack fluid) {

        return fluid.getFluid() == net.minecraft.world.level.material.Fluids.WATER ? Potions.WATER : getPotionFromFluidTag(fluid.getTag());
    }

    public static Potion getPotionFromFluidTag(@Nullable CompoundTag tag) {

        return tag == null || !tag.contains(TAG_POTION) ? Potions.EMPTY : Potion.byName(tag.getString(TAG_POTION));
    }

    public static void addPotionTooltipStrings(FluidStack stack, List<Component> list) {

        ArrayList<Component> lores = new ArrayList<>();
        addPotionTooltip(stack, lores, 1.0F);
        list.addAll(lores);
    }

    public static void addPotionTooltip(FluidStack stack, List<Component> list) {

        addPotionTooltip(stack, list, 1.0F);
    }

    public static void addPotionTooltip(FluidStack stack, List<Component> lores, float durationFactor) {

        if (stack.isEmpty()) {
            return;
        }
        addPotionTooltip(PotionUtils.getAllEffects(stack.getTag()), lores, durationFactor);
    }

    public static void addPotionTooltip(List<MobEffectInstance> list, List<Component> lores, float durationFactor) {

        List<Pair<Attribute, AttributeModifier>> list1 = new ArrayList<>();
        if (list.isEmpty()) {
            lores.add(EMPTY_POTION);
        } else {
            for (MobEffectInstance effectinstance : list) {
                MutableComponent mutableComponent = Component.translatable(effectinstance.getDescriptionId());
                MobEffect effect = effectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }
                if (effectinstance.getAmplifier() > 0) {
                    mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + effectinstance.getAmplifier()));
                }
                if (effectinstance.getDuration() > 20) {
                    mutableComponent = Component.translatable("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(effectinstance, durationFactor));
                }
                lores.add(mutableComponent.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }
        if (!list1.isEmpty()) {
            lores.add(Component.empty());
            lores.add((Component.translatable("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));

            for (Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }
                if (d0 > 0.0D) {
                    lores.add((Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    lores.add((Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static final MutableComponent EMPTY_POTION = (Component.translatable("effect.none")).withStyle(ChatFormatting.GRAY);
    // endregion

    // region PROPERTY HELPERS
    public static int color(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack) : 0;
    }

    public static int luminosity(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getLightLevel(stack) : 0;
    }

    public static int density(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getDensity(stack) : 0;
    }

    public static int temperature(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getTemperature(stack) : 0;
    }

    public static int viscosity(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getFluidType().getViscosity(stack) : 0;
    }
    // endregion

}
