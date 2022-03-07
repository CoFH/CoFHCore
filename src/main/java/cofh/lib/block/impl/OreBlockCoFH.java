package cofh.lib.block.impl;

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

    public OreBlockCoFH() {

        this(Properties.of(Material.STONE, MaterialColor.STONE).strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
    }

    public OreBlockCoFH(MaterialColor color) {

        this(Properties.of(Material.STONE, color).strength(3.0F, 3.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());
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
