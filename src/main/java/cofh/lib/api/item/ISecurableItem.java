package cofh.lib.api.item;

import cofh.lib.util.helpers.SecurityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface ISecurableItem extends ICoFHItem {

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

}
