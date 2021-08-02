package cofh.lib.block.impl;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CakeBlockCoFH extends CakeBlock {

    protected final Food food;

    public CakeBlockCoFH(Properties properties, @Nonnull Food food) {

        super(properties);
        this.food = food;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (worldIn.isRemote) {
            ItemStack stack = player.getHeldItem(handIn);
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
            player.addStat(Stats.EAT_CAKE_SLICE);
            player.getFoodStats().addStats(food.getHealing(), food.getSaturation());

            for (Pair<EffectInstance, Float> pair : this.food.getEffects()) {
                if (!world.isRemote && pair.getFirst() != null && world.rand.nextFloat() < pair.getSecond()) {
                    player.addPotionEffect(new EffectInstance(pair.getFirst()));
                }
            }
            int i = state.get(BITES);
            if (i < 6) {
                world.setBlockState(pos, state.with(BITES, i + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            return ActionResultType.SUCCESS;
        }
    }

}
