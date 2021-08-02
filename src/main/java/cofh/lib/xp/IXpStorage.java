package cofh.lib.xp;

import cofh.lib.util.helpers.MathHelper;

public interface IXpStorage {

    default int receiveXPFloat(float maxReceive, boolean simulate) {

        int xp = (int) maxReceive;
        float chance = maxReceive - xp;
        if (MathHelper.RANDOM.nextFloat() < chance) {
            ++xp;
        }
        return receiveXp(xp, simulate);
    }

    int receiveXp(int maxReceive, boolean simulate);

    int extractXp(int maxExtract, boolean simulate);

    int getXpStored();

    int getMaxXpStored();

}
