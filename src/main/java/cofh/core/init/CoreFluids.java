package cofh.core.init;

import cofh.core.fluid.HoneyFluid;
import cofh.core.fluid.PotionFluid;
import cofh.core.fluid.XPFluid;

public class CoreFluids {

    private CoreFluids() {

    }

    public static void register() {

        HoneyFluid.create();
        // MilkFluid.create();
        PotionFluid.create();
        XPFluid.create();
    }

}
