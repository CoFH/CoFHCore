package cofh.lib.block.impl.crops;

import cofh.lib.block.IHarvestable;
import cofh.lib.util.Utils;
import cofh.lib.util.helpers.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.AGE_0_7;
import static cofh.lib.util.constants.Constants.CROPS_BY_AGE;
import static net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE;

public class CropsBlockCoFH extends CropBlock implements IHarvestable {

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

    protected ItemLike getCropItem() {

        return crop.get();
    }

    protected ItemLike getBaseSeedId() {

        return seed.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(getAgeProperty());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {

        if (!worldIn.isAreaLoaded(pos, 1)) {
            return;
        }
        if (worldIn.getRawBrightness(pos, 0) >= growLight) {
            if (!canHarvest(state)) {
                int age = getAge(state);
                float growthChance = MathHelper.maxF(getGrowthSpeed(this, worldIn, pos) * growMod, 0.1F);
                if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / growthChance) + 1) == 0)) {
                    int newAge = age + 1 == getPostHarvestAge() ? getMaxAge() : age + 1;
                    worldIn.setBlock(pos, getStateForAge(newAge), 2);
                    ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (handIn == InteractionHand.MAIN_HAND && canHarvest(state)) {
            return harvest(worldIn, pos, state, player, false) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    // TODO: Revisit; vanilla crop logic effectively overrides
    //    @Override
    //    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    //
    //        return (worldIn.getLightSubtracted(pos, 0) >= growLight - 1 || worldIn.isSkyLightMax(pos)) && super.isValidPosition(state, worldIn, pos);
    //    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return CROPS_BY_AGE[MathHelper.clamp(state.getValue(getAgeProperty()), 0, CROPS_BY_AGE.length - 1)];
    }

    public static float getGrowthChanceProxy(Block blockIn, BlockGetter worldIn, BlockPos pos) {

        return getGrowthSpeed(blockIn, worldIn, pos);
    }

    // region AGE
    @Override
    public IntegerProperty getAgeProperty() {

        return AGE_0_7;
    }

    @Override
    protected int getAge(BlockState state) {

        return state.getValue(getAgeProperty());
    }

    protected int getPostHarvestAge() {

        return -1;
    }

    public BlockState getStateForAge(int age) {

        return defaultBlockState().setValue(getAgeProperty(), age);
    }
    // endregion

    // region IHarvestable
    @Override
    public boolean canHarvest(BlockState state) {

        return isMaxAge(state);
    }

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
            world.setBlock(pos, getStateForAge(getPostHarvestAge()), 2);
        } else {
            if (replant) {
                List<ItemStack> drops = Block.getDrops(state, (ServerLevel) world, pos, null, player, player.getMainHandItem());
                boolean seedDrop = false;
                Item seedItem = seed.get();
                for (ItemStack drop : drops) {
                    if (!seedDrop && drop.getItem() == seedItem) {
                        drop.shrink(1);
                        seedDrop = true;
                    }
                    if (!drop.isEmpty()) {
                        Utils.dropItemStackIntoWorldWithRandomness(drop, world, pos);
                    }
                }
                world.destroyBlock(pos, false, player);
                if (seedDrop) {
                    world.setBlock(pos, this.getStateForAge(0), 3);
                }
            } else {
                world.destroyBlock(pos, true, player);
            }
        }
        return true;
    }
    // endregion

    // region IGrowable
    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient) {

        return !canHarvest(state);
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state) {

        return true;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state) {

        if (canHarvest(state)) {
            return;
        }
        int postHarvest = getPostHarvestAge();
        int age = getAge(state);
        int newAge = age + getBonemealAgeIncrease(worldIn);

        if (age < postHarvest && newAge >= postHarvest) {
            worldIn.setBlock(pos, getStateForAge(getMaxAge()), 2);
        } else {
            worldIn.setBlock(pos, getStateForAge(Math.min(newAge, getMaxAge())), 2);
        }
    }
    // endregion

    // region IPlantable
    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {

        return type;
    }
    // endregion
}
