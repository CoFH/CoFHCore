package cofh.lib.block.impl;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CakeBlockCoFH extends CakeBlock {

    protected static final VoxelShape[] SHAPE_BY_BITE_TALL = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(3.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(5.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(7.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(9.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(11.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(13.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D)};

    protected boolean tall;

    protected final Food food;

    public CakeBlockCoFH(Properties properties, @Nonnull Food food) {

        super(properties);
        this.food = food;
    }

    public CakeBlockCoFH setTall() {

        this.tall = true;
        return this;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {

        return tall ? SHAPE_BY_BITE_TALL[state.getValue(BITES)] : SHAPE_BY_BITE[state.getValue(BITES)];
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (worldIn.isClientSide) {
            ItemStack stack = player.getItemInHand(handIn);
            if (this.eatPiece(worldIn, pos, state, player) == ActionResultType.SUCCESS) {
                return ActionResultType.SUCCESS;
            }
            if (stack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }
        return this.eatPiece(worldIn, pos, state, player);
    }

    protected ActionResultType eatPiece(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        if (!player.canEat(false)) {
            return ActionResultType.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());

            for (Pair<EffectInstance, Float> pair : this.food.getEffects()) {
                if (!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                    player.addEffect(new EffectInstance(pair.getFirst()));
                }
            }
            int i = state.getValue(BITES);
            if (i < 6) {
                world.setBlock(pos, state.setValue(BITES, i + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            return ActionResultType.SUCCESS;
        }
    }

}
