package cofh.core.client.settings;

import cofh.core.network.packet.server.ItemModeChangePacket;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;

public class KeyBindingModeChange extends KeyBinding {

    protected boolean isPressed; // Not strictly necessary but avoids an AT at this time.

    public KeyBindingModeChange(String description, int keyCode, String category) {

        super(description, keyCode, category);
        setKeyConflictContext(KeyConflictContext.IN_GAME);
    }

    public void setPressed(boolean valueIn) {

        super.setPressed(valueIn);
        isPressed = valueIn;
    }

    public static class Increment extends KeyBindingModeChange {

        public Increment(String description, int keyCode, String category) {

            super(description, keyCode, category);
        }

        @Override
        public void setPressed(boolean valueIn) {

            boolean prevPressed = isPressed;
            super.setPressed(valueIn);

            if (isPressed && !prevPressed) {
                ItemModeChangePacket.incrMode();
            }
        }

    }

    public static class Decrement extends KeyBindingModeChange {

        public Decrement(String description, int keyCode, String category) {

            super(description, keyCode, category);
        }

        @Override
        public void setPressed(boolean valueIn) {

            boolean prevPressed = isPressed;
            super.setPressed(valueIn);

            if (isPressed && !prevPressed) {
                ItemModeChangePacket.decrMode();
            }
        }

    }

}
