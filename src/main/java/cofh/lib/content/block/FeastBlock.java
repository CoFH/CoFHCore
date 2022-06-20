package cofh.lib.content.block;

import cofh.lib.util.helpers.MathHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static cofh.lib.util.Constants.BITES_0_7;

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

    protected final FoodProperties food;
    protected Supplier<Item> serving = () -> Items.AIR;

    public FeastBlock(Properties properties, @Nonnull FoodProperties food) {

        super(properties);
        this.food = food;
    }

    public FeastBlock serving(Supplier<Item> serving) {

        this.serving = serving;
        return this;
    }

    protected ItemLike getServingItem() {

        return serving.get();
    }

    public IntegerProperty getBitesProperty() {

        return BITES_0_7;
    }

    protected int getMaxBites() {

        return getBitesProperty().getPossibleValues().size() - 1;
    }

    protected InteractionResult serve(Level world, BlockPos pos, BlockState state, Player player) {

        ItemStack servItem = new ItemStack(getServingItem());
        boolean takeServing = !servItem.isEmpty() && !player.isSecondaryUseActive();

        if (!player.canEat(takeServing)) {
            return InteractionResult.PASS;
        } else {
            if (takeServing) {
                player.addItem(servItem);
            } else {
                player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
                for (Pair<MobEffectInstance, Float> pair : this.food.getEffects()) {
                    if (!world.isClientSide && pair.getFirst() != null && world.random.nextFloat() < pair.getSecond()) {
                        player.addEffect(new MobEffectInstance(pair.getFirst()));
                    }
                }
            }
            int i = state.getValue(getBitesProperty());
            if (i < getMaxBites()) {
                world.setBlock(pos, state.setValue(getBitesProperty(), i + 1), 3);
            } else {
                world.removeBlock(pos, false);
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (worldIn.isClientSide) {
            ItemStack stack = player.getItemInHand(handIn);
            if (this.serve(worldIn, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (stack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return this.serve(worldIn, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        return SHAPE_BY_BITE[MathHelper.clamp(state.getValue(getBitesProperty()), 0, SHAPE_BY_BITE.length - 1)];
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {

        return facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState stateIn, LevelReader worldIn, BlockPos pos) {

        return worldIn.getBlockState(pos.below()).getMaterial().isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        super.createBlockStateDefinition(builder);
        builder.add(getBitesProperty());
    }

    //    @Override
    //    public int getAnalogOutputSignal(BlockState stateIn, Level levelIn, BlockPos pos) {
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
    public boolean isPathfindable(BlockState stateIn, BlockGetter worldIn, BlockPos pos, PathComputationType type) {

        return false;
    }

}
