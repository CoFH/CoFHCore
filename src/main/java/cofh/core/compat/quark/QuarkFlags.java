package cofh.core.compat.quark;

import cofh.lib.util.flags.FlagManager;

import static cofh.lib.util.Utils.isModLoaded;
import static cofh.lib.util.constants.Constants.ID_QUARK;

public class QuarkFlags {

    private QuarkFlags() {

    }

    public static void setup() {

        if (!isModLoaded(ID_QUARK)) {
            new FlagManager(ID_QUARK);
        }
    }

}
