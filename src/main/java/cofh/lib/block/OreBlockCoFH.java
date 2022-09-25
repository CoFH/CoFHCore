package cofh.lib.block;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class OreBlockCoFH extends Block {

    protected int minXp = 0;
    protected int maxXp = 0;

    public static OreBlockCoFH createStoneOre() {

        return new OreBlockCoFH(Properties.of(Material.STONE, MaterialColor.STONE).strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    }

    public static OreBlockCoFH createDeepslateOre() {

        return new OreBlockCoFH(Properties.of(Material.STONE, MaterialColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
    }

    public OreBlockCoFH(Properties properties) {

        super(properties);
    }

    public OreBlockCoFH xp(int minXp, int maxXp) {

        this.minXp = minXp;
        this.maxXp = maxXp;
        return this;
    }

    protected int getExperience() {

        if (maxXp <= 0) {
            return 0;
        }
        if (minXp >= maxXp) {
            return minXp;
        }
        return MathHelper.nextInt(minXp, maxXp);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {

        return silktouch == 0 ? getExperience() : 0;
    }

}
