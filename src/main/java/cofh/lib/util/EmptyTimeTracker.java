package cofh.lib.util;

import net.minecraft.world.level.Level;

/**
 * A basic time tracker class. Nothing surprising here.
 *
 * @author King Lemming
 */
public class EmptyTimeTracker extends TimeTracker {

    public static final EmptyTimeTracker INSTANCE = new EmptyTimeTracker();

    @Override
    public boolean hasDelayPassed(Level world, int delay) {

        return false;
    }

    @Override
    public void markTime(Level world) {

    }

    @Override
    public boolean notSet() {

        return true;
    }

}
