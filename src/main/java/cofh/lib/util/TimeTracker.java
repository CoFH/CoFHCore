package cofh.lib.util;

import net.minecraft.world.level.Level;

/**
 * A basic time tracker class. Nothing surprising here.
 *
 * @author King Lemming
 */
public class TimeTracker {

    private long lastMark = Long.MIN_VALUE;

    public boolean hasDelayPassed(Level world, int delay) {

        long currentTime = world.getGameTime();

        if (currentTime < lastMark) {
            lastMark = currentTime;
            return false;
        } else if (lastMark + delay <= currentTime) {
            lastMark = currentTime;
            return true;
        }
        return false;
    }

    public void markTime(Level world) {

        lastMark = world.getGameTime();
    }

    public boolean notSet() {

        return lastMark == Long.MIN_VALUE;
    }

}
