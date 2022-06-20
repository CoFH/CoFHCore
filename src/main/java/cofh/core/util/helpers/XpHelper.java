package cofh.core.util.helpers;

import cofh.core.xp.IXpContainerItem;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static cofh.lib.util.NBTTags.TAG_XP_TIMER;

public class XpHelper {

    private XpHelper() {

    }

    public static int getPlayerXP(Player player) {

        return getTotalXpForLevel(player.experienceLevel) + getExtraPlayerXp(player);
    }

    public static int getLevelPlayerXP(Player player) {

        return getTotalXpForLevel(player.experienceLevel);
    }

    public static int getExtraPlayerXp(Player player) {

        return Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static void setPlayerXP(Player player, int exp) {

        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;
        player.totalExperience = 0;

        addXPToPlayer(player, exp);
    }

    public static void setPlayerLevel(Player player, int level) {

        player.experienceLevel = level;
        player.experienceProgress = 0.0F;
    }

    public static void addXPToPlayer(Player player, int exp) {

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

    public static void addXPLevelToPlayer(Player player, int levels) {

        player.experienceLevel += levels;

        if (player.experienceLevel < 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
            player.totalExperience = 0;
        }
    }

    public static void attemptStoreXP(Player player, ExperienceOrb orb) {

        // Xp Storage Items
        if (player.level.getGameTime() - player.getPersistentData().getLong(TAG_XP_TIMER) <= 40) {
            Inventory inventory = player.getInventory();
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
