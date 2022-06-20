package cofh.core.content.block.impl.crops;

import cofh.core.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.List;

import static cofh.lib.util.Constants.*;
import static net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE;

public class CropBlockTall extends CropBlockCoFH {

    public CropBlockTall(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder, type, growLight, growMod);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(TOP, false));
    }

    public CropBlockTall(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropBlockTall(Properties builder) {

        this(builder, 9, 1.25F);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(TOP);
    }

    protected boolean isTop(BlockState state) {

        return state.getValue(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

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
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {

        if (!worldIn.isAreaLoaded(pos, 1) || isTop(state) || !canSurvive(state, worldIn, pos)) {
            return;
        }
        if (worldIn.getRawBrightness(pos, 0) >= growLight) {
            if (!canHarvest(state)) {
                int age = getAge(state);
                float growthChance = MathHelper.maxF(getGrowthSpeed(this, worldIn, pos) * growMod, 0.1F);
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
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
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {

        if (isTop(state)) {
            return worldIn.getBlockState(pos.below()).getBlock() == this;
        }
        if (getAge(state) >= getTallAge()) {
            return worldIn.getBlockState(pos.above()).getBlock() == this && super.canSurvive(state, worldIn, pos);
        }
        return pos.getY() < worldIn.getMaxBuildHeight() && super.canSurvive(state, worldIn, pos) && (worldIn.isEmptyBlock(pos.above()));
    }

    // region BonemealableBlock
    @Override
    public boolean isBonemealSuccess(Level worldIn, RandomSource rand, BlockPos pos, BlockState state) {

        return !isTop(state);
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {

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
    public boolean harvest(Level world, BlockPos pos, BlockState state, Player player, boolean replant) {

        if (!canHarvest(state)) {
            return false;
        }
        if (Utils.isClientWorld(world)) {
            return true;
        }
        if (getPostHarvestAge() >= 0) {
            int fortune = Utils.getItemEnchantmentLevel(BLOCK_FORTUNE, player.getMainHandItem());
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
                Item seedItem = seed.get();

                List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, null, player, player.getMainHandItem());
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
