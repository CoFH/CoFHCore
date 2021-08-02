package cofh.core.init;

import cofh.lib.util.flags.FlagManager;

import java.util.function.BooleanSupplier;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

public class CoreFlags {

    private CoreFlags() {

    }

    private static final FlagManager FLAG_MANAGER = new FlagManager(ID_COFH_CORE);

    public static FlagManager manager() {

        return FLAG_MANAGER;
    }

    public static void setFlag(String flag, boolean enable) {

        FLAG_MANAGER.setFlag(flag, enable);
    }

    public static void setFlag(String flag, BooleanSupplier condition) {

        FLAG_MANAGER.setFlag(flag, condition);
    }

    public static BooleanSupplier getFlag(String flag) {

        return FLAG_MANAGER.getFlag(flag);
    }

    // region SPECIFIC FEATURES
    public static String FLAG_ECTOPLASM = "ectoplasm";
    // endregion
}
