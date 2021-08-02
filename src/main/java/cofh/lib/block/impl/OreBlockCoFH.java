package cofh.lib.block.impl;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public class OreBlockCoFH extends Block {

    protected int minXp = 0;
    protected int maxXp = 0;

    public OreBlockCoFH(int harvestLevel) {

        this(Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(3.0F, 3.0F).sound(SoundType.STONE).harvestLevel(harvestLevel).harvestTool(ToolType.PICKAXE).setRequiresTool());
    }

    public OreBlockCoFH(MaterialColor color, int harvestLevel) {

        this(Properties.create(Material.ROCK, color).hardnessAndResistance(3.0F, 3.0F).sound(SoundType.STONE).harvestLevel(harvestLevel).harvestTool(ToolType.PICKAXE).setRequiresTool());
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
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch) {

        return silktouch == 0 ? getExperience() : 0;
    }

}
