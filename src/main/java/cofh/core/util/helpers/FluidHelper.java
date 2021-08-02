package cofh.core.util.helpers;

import cofh.core.fluid.PotionFluid;
import cofh.lib.fluid.FluidStorageCoFH;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.references.CoreReferences;
import cofh.lib.util.references.FluidTagsCoFH;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static cofh.lib.util.constants.Constants.BOTTLE_VOLUME;
import static cofh.lib.util.constants.Constants.BUCKET_VOLUME;
import static cofh.lib.util.constants.NBTTags.TAG_POTION;
import static cofh.lib.util.references.CoreReferences.FLUID_HONEY;
import static cofh.lib.util.references.CoreReferences.FLUID_XP;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidHelper {

    private FluidHelper() {

    }

    public static final Predicate<FluidStack> IS_WATER = e -> e.getFluid().equals(Fluids.WATER);
    public static final Predicate<FluidStack> IS_LAVA = e -> e.getFluid().equals(Fluids.LAVA);
    public static final Predicate<FluidStack> IS_XP = e -> e.getFluid().equals(CoreReferences.FLUID_XP);

    // TODO: Revisit
    //    public static final Object2IntMap<Fluid> COLOR_CACHE = new Object2IntOpenHashMap<>();
    //
    //    public static int getFluidColor(FluidStack stack) {
    //
    //        int color = -1;
    //        if (stack != null && stack.getFluid() != null) {
    //            color = COLOR_CACHE.getOrDefault(stack.getFluid(), stack.getFluid().getAttributes().getColor(stack) != 0xFFFFFFFF ? stack.getFluid().getAttributes().getColor(stack) : ColorHelper.getColorFrom(stack.getFluid().getAttributes().getStillTexture(stack)));
    //            if (!COLOR_CACHE.containsKey(stack.getFluid())) COLOR_CACHE.put(stack.getFluid(), color);
    //        }
    //        return color;
    //    }
    //
    //    public static int getFluidColor(Fluid fluid) {
    //
    //        int color = -1;
    //        if (fluid != null) {
    //            color = COLOR_CACHE.getOrDefault(fluid, fluid.getAttributes().getColor() != 0xFFFFFFFF ? fluid.getAttributes().getColor() : ColorHelper.getColorFrom(fluid.getAttributes().getStillTexture()));
    //            if (!COLOR_CACHE.containsKey(fluid)) COLOR_CACHE.put(fluid, color);
    //        }
    //        return color;
    //    }

    public static int fluidHashcodeNoTag(FluidStack stack) {

        return stack.getFluid().hashCode();
    }

    public static int fluidHashcode(FluidStack stack) {

        return stack.getTag() != null ? stack.getFluid().hashCode() + 31 * stack.getTag().hashCode() : stack.getFluid().hashCode();
    }

    // region COMPARISON
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
    public static boolean extractFromAdjacent(TileEntity tile, FluidStorageCoFH tank, int amount, Direction side) {

        amount = Math.min(amount, tank.getSpace());
        if (!tank.getFluidStack().isEmpty()) {
            return extractFromAdjacent(tile, tank, new FluidStack(tank.getFluidStack(), amount), side);
        }
        TileEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
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

    public static boolean extractFromAdjacent(TileEntity tile, FluidStorageCoFH tank, FluidStack resource, Direction side) {

        TileEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
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

    public static boolean insertIntoAdjacent(TileEntity tile, FluidStorageCoFH tank, int amount, Direction side) {

        if (tank.isEmpty()) {
            return false;
        }
        amount = Math.min(amount, tank.getAmount());

        TileEntity adjTile = BlockHelper.getAdjacentTileEntity(tile, side);
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

    public static boolean hasFluidHandlerCap(TileEntity tile, Direction face) {

        return tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).isPresent();
    }

    public static IFluidHandler getFluidHandlerCap(TileEntity tile, Direction face) {

        return tile == null ? EmptyFluidHandler.INSTANCE : tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face).orElse(EmptyFluidHandler.INSTANCE);
    }
    // endregion

    // region CAPABILITY HELPERS
    public static boolean hasFluidHandlerCap(ItemStack item) {

        return !item.isEmpty() && item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
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
    public static boolean drainBottleToHandler(ItemStack stack, IFluidHandler handler, PlayerEntity player, Hand hand) {

        FluidStack fluid = FluidStack.EMPTY;

        if (stack.getItem() == Items.POTION) {
            fluid = PotionFluid.getPotionFluidFromItem(BOTTLE_VOLUME, stack);
        } else if (stack.getItem() == Items.HONEY_BOTTLE) {
            fluid = new FluidStack(FLUID_HONEY, BOTTLE_VOLUME);
        } else if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
            fluid = new FluidStack(FLUID_XP, BOTTLE_VOLUME);
        }
        return !fluid.isEmpty() && addEmptyBottleToPlayer(stack, fluid, handler, player, hand);
    }

    private static boolean addEmptyBottleToPlayer(ItemStack stack, FluidStack fluid, IFluidHandler handler, PlayerEntity player, Hand hand) {

        if (handler.fill(fluid, SIMULATE) == BOTTLE_VOLUME) {
            handler.fill(fluid, EXECUTE);
            if (!player.abilities.isCreativeMode) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                player.setHeldItem(hand, ItemHelper.consumeItem(stack, 1));
                if (!player.addItemStackToInventory(bottle)) {
                    player.dropItem(bottle, false);
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
    public static boolean fillBottleFromHandler(ItemStack stack, IFluidHandler handler, PlayerEntity player, Hand hand) {

        if (stack.getItem() != Items.GLASS_BOTTLE) {
            return false;
        }
        FluidStack fluid = handler.drain(BOTTLE_VOLUME, SIMULATE);
        if (fluid.getAmount() != BOTTLE_VOLUME) {
            return false;
        }
        ItemStack bottle = ItemStack.EMPTY;

        if (fluid.getFluid() == Fluids.WATER || hasPotionTag(fluid)) {
            bottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), getPotionFromFluid(fluid));
        } else if (fluid.getFluid().isIn(FluidTagsCoFH.HONEY)) {
            bottle = new ItemStack(Items.HONEY_BOTTLE);
        } else if (fluid.getFluid().isIn(FluidTagsCoFH.EXPERIENCE)) {
            bottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
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
        return fluid.getFluid().getFilledBucket() != Items.AIR;
    }

    private static boolean addFilledBottleToPlayer(ItemStack stack, ItemStack bottle, IFluidHandler handler, PlayerEntity player, Hand hand) {

        if (handler.drain(BOTTLE_VOLUME, EXECUTE).getAmount() == BOTTLE_VOLUME) {
            if (!player.abilities.isCreativeMode) {
                player.setHeldItem(hand, ItemHelper.consumeItem(stack, 1));
                if (!player.addItemStackToInventory(bottle)) {
                    player.dropItem(bottle, false);
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
    public static boolean drainItemToHandler(ItemStack stack, IFluidHandler handler, PlayerEntity player, Hand hand) {

        if (stack.isEmpty() || handler == null || player == null) {
            return false;
        }
        if (drainBottleToHandler(stack, handler, player, hand)) {
            player.world.playSound(null, player.getPosX(), player.getPosY() + 0.5, player.getPosZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        IItemHandler playerInv = new InvWrapper(player.inventory);
        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player, true);
        if (result.isSuccess()) {
            player.setHeldItem(hand, result.getResult());
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
    public static boolean fillItemFromHandler(ItemStack stack, IFluidHandler handler, PlayerEntity player, Hand hand) {

        if (stack.isEmpty() || handler == null || player == null || !bucketFillTest(stack, handler)) {
            return false;
        }
        if (fillBottleFromHandler(stack, handler, player, hand)) {
            player.world.playSound(null, player.getPosX(), player.getPosY() + 0.5, player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        IItemHandler playerInv = new InvWrapper(player.inventory);
        FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player, true);
        if (result.isSuccess()) {
            player.setHeldItem(hand, result.getResult());
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
    public static boolean interactWithHandler(ItemStack stack, IFluidHandler handler, PlayerEntity player, Hand hand) {

        return drainItemToHandler(stack, handler, player, hand) || fillItemFromHandler(stack, handler, player, hand);
    }
    // endregion

    // region POTION HELPERS
    public static boolean hasPotionTag(FluidStack stack) {

        return !stack.isEmpty() && stack.getTag() != null && stack.getTag().contains(TAG_POTION);
    }

    public static Potion getPotionFromFluid(FluidStack fluid) {

        return fluid.getFluid() == Fluids.WATER ? Potions.WATER : getPotionFromFluidTag(fluid.getTag());
    }

    public static Potion getPotionFromFluidTag(@Nullable CompoundNBT tag) {

        return tag == null || !tag.contains(TAG_POTION) ? Potions.EMPTY : Potion.getPotionTypeForName(tag.getString(TAG_POTION));
    }

    public static void addPotionTooltipStrings(FluidStack stack, List<ITextComponent> list) {

        ArrayList<ITextComponent> lores = new ArrayList<>();
        addPotionTooltip(stack, lores, 1.0F);
        list.addAll(lores);
    }

    public static void addPotionTooltip(FluidStack stack, List<ITextComponent> list) {

        addPotionTooltip(stack, list, 1.0F);
    }

    public static void addPotionTooltip(FluidStack stack, List<ITextComponent> lores, float durationFactor) {

        if (stack.isEmpty()) {
            return;
        }
        addPotionTooltip(PotionUtils.getEffectsFromTag(stack.getTag()), lores, durationFactor);
    }

    public static void addPotionTooltip(List<EffectInstance> list, List<ITextComponent> lores, float durationFactor) {

        List<Pair<Attribute, AttributeModifier>> list1 = new ArrayList<>();
        if (list.isEmpty()) {
            lores.add(EMPTY_POTION);
        } else {
            for (EffectInstance effectinstance : list) {
                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getEffectName());
                Effect effect = effectinstance.getPotion();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifierMap();
                if (!map.isEmpty()) {
                    for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierAmount(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }
                if (effectinstance.getAmplifier() > 0) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
                }
                if (effectinstance.getDuration() > 20) {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.getPotionDurationString(effectinstance, durationFactor));
                }
                lores.add(iformattabletextcomponent.mergeStyle(effect.getEffectType().getColor()));
            }
        }
        if (!list1.isEmpty()) {
            lores.add(StringTextComponent.EMPTY);
            lores.add((new TranslationTextComponent("potion.whenDrank")).mergeStyle(TextFormatting.DARK_PURPLE));

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
                    lores.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    lores.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.RED));
                }
            }
        }
    }

    public static final IFormattableTextComponent EMPTY_POTION = (new TranslationTextComponent("effect.none")).mergeStyle(TextFormatting.GRAY);
    // endregion

    // region PROPERTY HELPERS
    public static int color(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getColor(stack) : 0;
    }

    public static int luminosity(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getLuminosity(stack) : 0;
    }

    public static int density(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getDensity(stack) : 0;
    }

    public static boolean gaseous(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null && stack.getFluid().getAttributes().isGaseous();
    }

    public static int temperature(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getTemperature(stack) : 0;
    }

    public static int viscosity(FluidStack stack) {

        return !stack.isEmpty() && stack.getFluid() != null ? stack.getFluid().getAttributes().getViscosity(stack) : 0;
    }
    // endregion

}