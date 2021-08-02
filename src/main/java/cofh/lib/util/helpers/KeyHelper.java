package cofh.lib.util.helpers;

import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class KeyHelper {

    private KeyHelper() {

    }

    @Nullable
    public static String getKeynameFromKeycode(int p_216507_0_) {

        return GLFW.glfwGetKeyName(p_216507_0_, -1);
    }

    @Nullable
    public static String getKeyNameFromScanCode(int p_216502_0_) {

        return GLFW.glfwGetKeyName(-1, p_216502_0_);
    }

}
