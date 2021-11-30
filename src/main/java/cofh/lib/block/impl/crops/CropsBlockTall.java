package cofh.lib.block.impl.crops;

import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;

import static cofh.lib.util.constants.Constants.*;
import static net.minecraft.enchantment.Enchantments.BLOCK_FORTUNE;

public class CropsBlockTall extends CropsBlockCoFH {

    public CropsBlockTall(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(TOP, false));
    }

    public CropsBlockTall(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropsBlockTall(Properties builder) {

        this(builder, 9, 1.25F);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    protected boolean isTop(BlockState state) {

        return state.getValue(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

        int age = state.getValue(getAgeProperty()) - (isTop(state) ? 2 : 0);
        return TALL_CROPS_BY_AGE[MathHelper.clamp(age, 0, TALL_CROPS_BY_AGE.length - 1)];
    }

    @Override
    public IntegerProperty getAgeProperty() {

        return AGE_0_9;
    }

    @Override
    public int getMaxAge() {

        return 9;
    }

    protected int getTallAge() {

        return 4;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        if (!worldIn.isAreaLoaded(pos, 1) || isTop(state) || !canSurvive(state, worldIn, pos)) {
            return;
        }
        if (worldIn.getRawBrightness(pos, 0) >= growLight) {
            if (!canHarvest(state)) {
                int age = getAge(state);
                float growthChance = MathHelper.maxF(getGrowthSpeed(this, worldIn, pos) * growMod, 0.1F);
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
                    int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
                    worldIn.setBlock(pos, getStateForAge(newAge), 2);
                    if (newAge >= getTallAge()) {
                        worldIn.setBlock(pos.above(), getStateForAge(newAge).setValue(TOP, true), 2);
                    }
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {

        if (isTop(state)) {
            return worldIn.getBlockState(pos.below()).getBlock() == this;
        }
        if (getAge(state) >= getTallAge()) {
            return worldIn.getBlockState(pos.above()).getBlock() == this;
        }
        return pos.getY() < 255 && super.canSurvive(state, worldIn, pos) && (worldIn.isEmptyBlock(pos.above()));
    }

    // region IGrowable
    @Override
    public boolean isBonemealSuccess(World worldIn, Random rand, BlockPos pos, BlockState state) {

        return !isTop(state);
    }

    @Override
    public void performBonemeal(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {

        if (canHarvest(state) || isTop(state) || !canSurvive(state, worldIn, pos)) {
            return;
        }
        BlockPos above = pos.above();
        if (!worldIn.isEmptyBlock(above) && worldIn.getBlockState(above).getBlock() != this) {
            return;
        }
        int newAge = getAge(state) + getBonemealAgeIncrease(worldIn);
        newAge = Math.min(newAge, getMaxAge());
        worldIn.setBlock(pos, getStateForAge(newAge), 2);
        if (newAge >= getTallAge()) {
            worldIn.setBlock(above, getStateForAge(newAge).setValue(TOP, true), 2);
        }
    }
    // endregion

    // region IHarvestable
    @Override
    public boolean harvest(World world, BlockPos pos, BlockState state, PlayerEntity player, boolean replant) {

        if (!canHarvest(state)) {
            return false;
        }
        if (Utils.isClientWorld(world)) {
            return true;
        }
        if (getPostHarvestAge() >= 0) {
            int fortune = EnchantmentHelper.getItemEnchantmentLevel(BLOCK_FORTUNE, player.getMainHandItem());
            Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos);
            if (isTop(state)) {
                world.setBlock(pos, this.getStateForAge(getPostHarvestAge() + getTallAge()), 2);
                world.setBlock(pos.below(), this.getStateForAge(getPostHarvestAge()), 2);
                Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos.below());
            } else {
                world.setBlock(pos, this.getStateForAge(getPostHarvestAge()), 2);
                world.setBlock(pos.above(), this.getStateForAge(getPostHarvestAge() + getTallAge()), 2);
                Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos.above());
            }
        } else {
            if (replant) {
                boolean seedDrop = false;
                Item seedItem = seed.get().getItem();

                List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, pos, null, player, player.getMainHandItem());
                for (ItemStack drop : drops) {
                    drop.setCount(drop.getCount() * 2);

                    if (!seedDrop && drop.getItem() == seedItem) {
                        drop.shrink(1);
                        seedDrop = true;
                    }
                    if (!drop.isEmpty()) {
                        Utils.dropItemStackIntoWorldWithRandomness(drop, world, pos);
                    }
                    world.destroyBlock(pos, false, player);
                    world.destroyBlock(isTop(state) ? pos.below() : pos.above(), false, player);
                    if (seedDrop) {
                        world.setBlock(isTop(state) ? pos.below() : pos, getStateForAge(0), 3);
                    }
                }
            } else {
                world.destroyBlock(pos, true, player);
                world.destroyBlock(isTop(state) ? pos.below() : pos.above(), true, player);
            }
        }
        return true;
    }
    // endregion
}
