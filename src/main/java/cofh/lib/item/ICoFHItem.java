package cofh.lib.item;

import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.init.CoreKeys.MULTIMODE_DECREMENT;
import static cofh.core.init.CoreKeys.MULTIMODE_INCREMENT;
import static cofh.lib.util.constants.NBTTags.*;
import static cofh.lib.util.helpers.AugmentableHelper.getPropertyWithDefault;
import static cofh.lib.util.helpers.KeyHelper.getKeynameFromKeycode;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;

/**
 * Hacky default interface to reduce boilerplate. :)
 */
public interface ICoFHItem extends IForgeItem {

    default boolean isCreative(ItemStack stack, ContainerType type) {

        switch (type) {
            case ITEM:
                return getPropertyWithDefault(stack, TAG_AUGMENT_ITEM_CREATIVE, 0.0F) > 0;
            case FLUID:
                return getPropertyWithDefault(stack, TAG_AUGMENT_FLUID_CREATIVE, 0.0F) > 0;
            case ENERGY:
                return getPropertyWithDefault(stack, TAG_AUGMENT_RF_CREATIVE, 0.0F) > 0;
        }
        return false;
    }

    default boolean canPlayerAccess(ItemStack stack, Player player) {

        return SecurityHelper.getAccess(stack).matches(SecurityHelper.getOwner(stack), player);
    }

    @Override
    default boolean hasCustomEntity(ItemStack stack) {

        return SecurityHelper.hasSecurity(stack);
    }

    @Override
    @Nullable
    default Entity createEntity(Level world, Entity location, ItemStack stack) {

        if (SecurityHelper.hasSecurity(stack)) {
            location.setInvulnerable(true);
            ((ItemEntity) location).lifespan = Integer.MAX_VALUE;
        }
        return null;
    }

    default void addEnergyTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn, int extract, int receive, boolean creative) {

        if (extract == receive && extract > 0 || creative) {
            tooltip.add(getTextComponent(localize("info.cofh.transfer") + ": " + getScaledNumber(extract) + " RF/t"));
        } else if (extract > 0) {
            if (receive > 0) {
                tooltip.add(getTextComponent(localize("info.cofh.send") + "|" + localize("info.cofh.receive") + ": " + getScaledNumber(extract) + "|" + getScaledNumber(receive) + " RF/t"));
            } else {
                tooltip.add(getTextComponent(localize("info.cofh.send") + ": " + getScaledNumber(extract) + " RF/t"));
            }
        } else if (receive > 0) {
            tooltip.add(getTextComponent(localize("info.cofh.receive") + ": " + getScaledNumber(receive) + " RF/t"));
        }
    }

    default void addModeChangeTooltip(IMultiModeItem item, ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        if (item.getNumModes(stack) <= 2) {
            addIncrementModeChangeTooltip(item, stack, worldIn, tooltip, flagIn);
            return;
        }
        tooltip.add(Component.translatable("info.cofh.mode_change", getKeynameFromKeycode(MULTIMODE_INCREMENT.getKey().getValue()), getKeynameFromKeycode(MULTIMODE_DECREMENT.getKey().getValue())).withStyle(YELLOW));

        if (MULTIMODE_INCREMENT.getKey().getValue() == -1) {
            tooltip.add(Component.translatable("info.cofh.key_not_bound", localize("key.cofh.mode_change_increment")).withStyle(RED));
        }
        if (MULTIMODE_DECREMENT.getKey().getValue() == -1) {
            tooltip.add(Component.translatable("info.cofh.key_not_bound", localize("key.cofh.mode_change_decrement")).withStyle(RED));
        }
    }

    default void addIncrementModeChangeTooltip(IMultiModeItem item, ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(Component.translatable("info.cofh.mode_toggle", getKeynameFromKeycode(MULTIMODE_INCREMENT.getKey().getValue())).withStyle(YELLOW));

        if (MULTIMODE_INCREMENT.getKey().getValue() == -1) {
            tooltip.add(Component.translatable("info.cofh.key_not_bound", localize("key.cofh.mode_change_increment")).withStyle(RED));
        }
    }

    default boolean isActive(ItemStack stack) {

        return stack.getOrCreateTag().getBoolean(TAG_ACTIVE);
    }

    default void setActive(ItemStack stack, boolean state) {

        stack.getOrCreateTag().putBoolean(TAG_ACTIVE, state);
    }

    default boolean hasActiveTag(ItemStack stack) {

        return stack.getOrCreateTag().contains(TAG_ACTIVE);
    }

    default void setActive(ItemStack stack, LivingEntity entity) {

        stack.getOrCreateTag().putLong(TAG_ACTIVE, entity.level.getGameTime() + 20);
    }

}
