package cofh.core.content.xp;

import cofh.lib.api.item.IContainerItem;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static cofh.lib.util.NBTTags.TAG_XP;

public interface IXpContainerItem extends IContainerItem {

    int getCapacityXP(ItemStack stack);

    default int getStoredXp(ItemStack stack) {

        return stack.getOrCreateTag().getInt(TAG_XP);
    }

    default int getSpaceXP(ItemStack stack) {

        return getCapacityXP(stack) - getStoredXp(stack);
    }

    default int modifyXp(ItemStack stack, int xp) {

        int totalXP = getStoredXp(stack) + xp;

        if (totalXP > getCapacityXP(stack)) {
            totalXP = getCapacityXP(stack);
        } else if (totalXP < 0) {
            totalXP = 0;
        }
        stack.getOrCreateTag().putInt(TAG_XP, totalXP);
        return totalXP;
    }

    static boolean storeXpOrb(Player player, ExperienceOrb orb, ItemStack stack) {

        IXpContainerItem item = (IXpContainerItem) stack.getItem();
        int toAdd = Math.min(item.getSpaceXP(stack), orb.value);

        if (toAdd > 0) {
            stack.setPopTime(5);
            player.level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (MathHelper.RANDOM.nextFloat() - MathHelper.RANDOM.nextFloat()) * 0.35F + 0.9F);
            item.modifyXp(stack, toAdd);
            orb.value -= toAdd;
            return true;
        }
        return false;
    }

}
