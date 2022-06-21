package cofh.lib.api.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.constants.NBTTags.TAG_ACTIVE;
import static cofh.lib.util.helpers.StringHelper.*;

/**
 * Hacky default interface to reduce boilerplate. :)
 */
public interface ICoFHItem extends IForgeItem {

    ICoFHItem setDisplayGroup(Supplier<CreativeModeTab> displayGroup);

    ICoFHItem setModId(String modId);

    ICoFHItem setShowInGroups(BooleanSupplier showInGroups);

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
