package cofh.lib.util.helpers;

import cofh.lib.xp.IXpContainerItem;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import static cofh.lib.util.constants.NBTTags.TAG_XP_TIMER;

public class XpHelper {

    private XpHelper() {

    }

    public static int getPlayerXP(PlayerEntity player) {

        return getTotalXpForLevel(player.experienceLevel) + getExtraPlayerXp(player);
    }

    public static int getLevelPlayerXP(PlayerEntity player) {

        return getTotalXpForLevel(player.experienceLevel);
    }

    public static int getExtraPlayerXp(PlayerEntity player) {

        return Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static void setPlayerXP(PlayerEntity player, int exp) {

        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;
        player.totalExperience = 0;

        addXPToPlayer(player, exp);
    }

    public static void setPlayerLevel(PlayerEntity player, int level) {

        player.experienceLevel = level;
        player.experienceProgress = 0.0F;
    }

    public static void addXPToPlayer(PlayerEntity player, int exp) {

        int i = Integer.MAX_VALUE - player.totalExperience;
        if (exp > i) {
            exp = i;
        }
        player.experienceProgress += (float) exp / (float) player.getXpNeededForNextLevel();
        for (player.totalExperience += exp; player.experienceProgress >= 1.0F; player.experienceProgress /= (float) player.getXpNeededForNextLevel()) {
            player.experienceProgress = (player.experienceProgress - 1.0F) * (float) player.getXpNeededForNextLevel();
            addXPLevelToPlayer(player, 1);
        }
    }

    public static void addXPLevelToPlayer(PlayerEntity player, int levels) {

        player.experienceLevel += levels;

        if (player.experienceLevel < 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
            player.totalExperience = 0;
        }
    }

    public static void attemptStoreXP(PlayerEntity player, ExperienceOrbEntity orb) {

        // Xp Storage Items
        if (player.level.getGameTime() - player.getPersistentData().getLong(TAG_XP_TIMER) <= 40) {
            PlayerInventory inventory = player.inventory;
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack stack = inventory.getItem(i);
                if (stack.getItem() instanceof IXpContainerItem && IXpContainerItem.storeXpOrb(player, orb, stack)) {
                    break;
                }
            }
        }
    }

    public static int getTotalXpForLevel(int level) {

        return level >= 32 ? (9 * level * level - 325 * level + 4440) / 2 : level >= 17 ? (5 * level * level - 81 * level + 720) / 2 : (level * level + 6 * level);
    }

}
