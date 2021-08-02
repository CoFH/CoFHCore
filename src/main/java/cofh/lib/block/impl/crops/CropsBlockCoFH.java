package cofh.lib.block.impl.crops;

import cofh.lib.block.IHarvestable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.Random;
import java.util.function.Supplier;

import static cofh.lib.util.Utils.getItemEnchantmentLevel;
import static cofh.lib.util.constants.Constants.AGE_0_7;
import static cofh.lib.util.constants.Constants.CROPS_BY_AGE;

public class CropsBlockCoFH extends CropsBlock implements IHarvestable {

    protected final PlantType type;
    protected int growLight;
    protected float growMod;

    protected Supplier<Item> crop = () -> Items.AIR;
    protected Supplier<Item> seed = () -> Items.AIR;

    public CropsBlockCoFH(Properties builder, PlantType type, int growLight, float growMod) {

        super(builder);
        this.type = type;
        this.growLight = growLight;
        this.growMod = growMod;
    }

    public CropsBlockCoFH(Properties builder, int growLight, float growMod) {

        this(builder, PlantType.CROP, growLight, growMod);
    }

    public CropsBlockCoFH(Properties builder) {

        this(builder, PlantType.CROP, 9, 1.0F);
    }

    public CropsBlockCoFH growMod(float growMod) {

        this.growMod = growMod;
        return this;
    }

    public CropsBlockCoFH crop(Supplier<Item> crop) {

        this.crop = crop;
        return this;
    }

    public CropsBlockCoFH seed(Supplier<Item> seed) {

        this.seed = seed;
        return this;
    }

    protected IItemProvider getCropItem() {

        return crop.get();
    }

    protected IItemProvider getSeedsItem() {

        return seed.get();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {

        builder.add(getAgeProperty());
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {

        if (!worldIn.isAreaLoaded(pos, 1)) {
            return;
        }
        if (worldIn.getLightSubtracted(pos, 0) >= growLight) {
            if (!canHarvest(state)) {
                int age = getAge(state);
                float growthChance = MathHelper.maxF(getGrowthChance(this, worldIn, pos) * growMod, 0.1F);
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
                    int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
                    worldIn.setBlockState(pos, withAge(newAge), 2);
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (player.getHeldItemMainhand().isEmpty()) {
            return harvest(worldIn, pos, state, getItemEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItem(handIn))) ? ActionResultType.SUCCESS : ActionResultType.PASS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    // TODO: Revisit; vanilla crop logic effectively overrides
    //    @Override
    //    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    //
    //        return (worldIn.getLightSubtracted(pos, 0) >= growLight - 1 || worldIn.isSkyLightMax(pos)) && super.isValidPosition(state, worldIn, pos);
    //    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {

        return CROPS_BY_AGE[MathHelper.clamp(state.get(getAgeProperty()), 0, CROPS_BY_AGE.length - 1)];
    }

    public static float getGrowthChanceProxy(Block blockIn, IBlockReader worldIn, BlockPos pos) {

        return getGrowthChance(blockIn, worldIn, pos);
    }

    // region AGE
    public IntegerProperty getAgeProperty() {

        return AGE_0_7;
    }

    @Override
    protected int getAge(BlockState state) {

        return state.get(getAgeProperty());
    }

    protected int getPostHarvestAge() {

        return -1;
    }

    public BlockState withAge(int age) {

        return getDefaultState().with(getAgeProperty(), age);
    }
    // endregion

    // region IHarvestable
    @Override
    public boolean canHarvest(BlockState state) {

        return isMaxAge(state);
    }

    @Override
    public boolean harvest(World world, BlockPos pos, BlockState state, int fortune) {

        if (!canHarvest(state)) {
            return false;
        }
        if (Utils.isClientWorld(world)) {
            return true;
        }
        if (getPostHarvestAge() >= 0) {
            Utils.dropItemStackIntoWorldWithRandomness(new ItemStack(getCropItem(), 2 + MathHelper.binomialDist(fortune, 0.5D)), world, pos);
            world.setBlockState(pos, withAge(getPostHarvestAge()), 2);
        } else {
            world.destroyBlock(pos, true);
        }
        return true;
    }
    // endregion

    // region IGrowable
    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {

        return !canHarvest(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {

        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {

        if (canHarvest(state)) {
            return;
        }
        int postHarvest = getPostHarvestAge();
        int age = getAge(state);
        int newAge = age + getBonemealAgeIncrease(worldIn);

        if (age < postHarvest && newAge >= postHarvest) {
            worldIn.setBlockState(pos, withAge(getMaxAge()), 2);
        } else {
            worldIn.setBlockState(pos, withAge(Math.min(newAge, getMaxAge())), 2);
        }
    }
    // endregion

    // region IPlantable
    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {

        return type;
    }
    // endregion
}