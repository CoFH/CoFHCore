package cofh.core.common.block;

import cofh.core.util.ProxyUtils;
import net.minecraft.Util;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraftforge.registries.ForgeRegistries;

public class CarvedPumpkinBlockCoFH extends CarvedPumpkinBlock {

    protected String translationKey = "";

    public CarvedPumpkinBlockCoFH setTranslationKey(String translationKey) {

        this.translationKey = translationKey;
        return this;
    }

    /**
     * This ensures that the predicate check isn't stupid. Can't do this for other hardcoded cases unfortunately.
     */
    public static void updatePredicate() {

        PUMPKINS_PREDICATE = (state) -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN) || state.getBlock() instanceof CarvedPumpkinBlockCoFH);
    }

    public CarvedPumpkinBlockCoFH(Properties properties) {

        super(properties);
    }

    @Override
    public String getDescriptionId() {

        String specificTranslation = Util.makeDescriptionId("block", ForgeRegistries.BLOCKS.getKey(this));
        if (ProxyUtils.canLocalize(specificTranslation)) {
            return specificTranslation;
        }
        return translationKey;
    }

}
