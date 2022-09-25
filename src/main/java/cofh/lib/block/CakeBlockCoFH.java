package cofh.lib.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class CakeBlockCoFH extends CakeBlock {

    protected static final VoxelShape[] SHAPE_BY_BITE_TALL = new VoxelShape[]{Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(3.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(5.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(7.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(9.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(11.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D), Block.box(13.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D)};

    protected boolean tall;

    protected final FoodProperties food;

    public CakeBlockCoFH(Properties properties, @Nonnull FoodProperties food) {

        super(properties);
        this.food = food;
    }

    public CakeBlockCoFH setTall() {

        this.tall = true;
        return this;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {

        return tall ? SHAPE_BY_BITE_TALL[state.getValue(BITES)] : SHAPE_BY_BITE[state.getValue(BITES)];
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (worldIn.isClientSide) {
            ItemStack stack = player.getItemInHand(handIn);
            if (this.eatPiece(worldIn, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (stack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return this.eatPiece(worldIn, pos, state, player);
    }

    protected InteractionResult eatPiece(Level world, BlockPos pos, BlockState state, Player player) {

        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());

            for (Pair<MobEffectInstance, Float> pair : this.food.getEffects()) {
                if (!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                    player.addEffect(new MobEffectInstance(pair.getFirst()));
                }
            }
            int i = state.getValue(BITES);
            if (i < 6) {
                world.setBlock(pos, state.setValue(BITES, i + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            return InteractionResult.SUCCESS;
        }
    }

}
