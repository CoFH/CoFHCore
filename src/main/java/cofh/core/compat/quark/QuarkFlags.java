package cofh.core.compat.quark;

import cofh.lib.util.flags.FlagManager;

import static cofh.lib.util.Constants.ID_QUARK;
import static cofh.lib.util.Utils.isModLoaded;

public class QuarkFlags {

    private QuarkFlags() {

    }

    public static void setup() {

        // This allows the "quark:flag" syntax to work as a recipe condition without Quark present.
        if (!isModLoaded(ID_QUARK)) {
            new FlagManager(ID_QUARK).setup();
        }
    }

}
