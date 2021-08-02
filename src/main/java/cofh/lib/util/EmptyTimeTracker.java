package cofh.lib.util;

import net.minecraft.world.World;

/**
 * A basic time tracker class. Nothing surprising here.
 *
 * @author King Lemming
 */
public class EmptyTimeTracker extends TimeTracker {

    public static final EmptyTimeTracker INSTANCE = new EmptyTimeTracker();

    @Override
    public boolean hasDelayPassed(World world, int delay) {

        return false;
    }

    @Override
    public void markTime(World world) {

    }

    @Override
    public boolean notSet() {

        return true;
    }

}
