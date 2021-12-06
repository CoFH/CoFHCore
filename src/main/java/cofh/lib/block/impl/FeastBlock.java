package cofh.lib.block.impl;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.BITES_0_7;

public class FeastBlock extends DirectionalBlock4Way {

    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D)};

    protected final Food food;
    protected Supplier<Item> serving = () -> Items.AIR;

    public FeastBlock(Properties properties, @Nonnull Food food) {

        super(properties);
        this.food = food;
    }

    public FeastBlock serving(Supplier<Item> serving) {

        this.serving = serving;
        return this;
    }

    protected IItemProvider getServingItem() {

        return serving.get();
    }

    public IntegerProperty getBitesProperty() {

        return BITES_0_7;
    }

    protected int getMaxBites() {

        return getBitesProperty().getPossibleValues().size() - 1;
    }

    protected ActionResultType serve(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        ItemStack servItem = new ItemStack(getServingItem());
        boolean takeServing = !servItem.isEmpty() && !player.isSecondaryUseActive();

        if (!player.canEat(takeServing)) {
            return ActionResultType.PASS;
        } else {
            if (takeServing) {
                player.addItem(servItem);
            } else {
                player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
                for (Pair<EffectInstance, Float> pair : this.food.getEffects()) {
                    if (!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                        player.addEffect(new EffectInstance(pair.getFirst()));
                    }
                }
            }
            int i = state.getValue(getBitesProperty());
            if (i < getMaxBites()) {
                world.setBlock(pos, state.setValue(getBitesProperty(), i + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (worldIn.isClientSide) {
            ItemStack stack = player.getItemInHand(handIn);
            if (this.serve(worldIn, pos, state, player).consumesAction()) {
                return ActionResultType.SUCCESS;
            }
            if (stack.isEmpty()) {
                return ActionResultType.CONSUME;
            }
        }
        return this.serve(worldIn, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {

        return SHAPE_BY_BITE[MathHelper.clamp(state.getValue(getBitesProperty()), 0, SHAPE_BY_BITE.length - 1)];
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {

        return facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState stateIn, IWorldReader worldIn, BlockPos pos) {

        return worldIn.getBlockState(pos.below()).getMaterial().isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(getBitesProperty());
    }

    //    @Override
    //    public int getAnalogOutputSignal(BlockState stateIn, World worldIn, BlockPos pos) {
    //
    //        return (8 - stateIn.getValue(getBitesProperty())) * 2;
    //    }
    //
    //    @Override
    //    public boolean hasAnalogOutputSignal(BlockState stateIn) {
    //
    //        return true;
    //    }

    @Override
    public boolean isPathfindable(BlockState stateIn, IBlockReader worldIn, BlockPos pos, PathType type) {

        return false;
    }

}
