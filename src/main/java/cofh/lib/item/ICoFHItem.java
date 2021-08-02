package cofh.lib.item;

import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;

import static cofh.core.init.CoreKeys.MULTIMODE_INCREMENT;
import static cofh.lib.util.constants.NBTTags.*;
import static cofh.lib.util.helpers.AugmentableHelper.getPropertyWithDefault;
import static cofh.lib.util.helpers.KeyHelper.getKeynameFromKeycode;
import static cofh.lib.util.helpers.StringHelper.*;
import static net.minecraft.util.text.TextFormatting.YELLOW;

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

    @Override
    default boolean hasCustomEntity(ItemStack stack) {

        return SecurityHelper.hasSecurity(stack);
    }

    @Override
    @Nullable
    default Entity createEntity(World world, Entity location, ItemStack stack) {

        if (SecurityHelper.hasSecurity(stack)) {
            location.setInvulnerable(true);
            ((ItemEntity) location).lifespan = Integer.MAX_VALUE;
        }
        return null;
    }

    default void addEnergyTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn, int extract, int receive, boolean creative) {

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

    default void addIncrementModeChangeTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent("info.cofh.mode_change", getKeynameFromKeycode(MULTIMODE_INCREMENT.getKey().getKeyCode())).mergeStyle(YELLOW));
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

        stack.getOrCreateTag().putLong(TAG_ACTIVE, entity.world.getGameTime() + 20);
    }

}
