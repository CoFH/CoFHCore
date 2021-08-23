package cofh.core.block;

import cofh.core.util.ProxyUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class CarvedPumpkinBlockCoFH extends CarvedPumpkinBlock {

    protected Supplier<Block> carvePrev;
    protected Supplier<Block> carveNext;

    protected String translationKey = "";

    public CarvedPumpkinBlockCoFH setTranslationKey(String translationKey) {

        this.translationKey = translationKey;
        return this;
    }

    /**
     * This ensures that the predicate check isn't stupid. Can't do this for other hardcoded cases unfortunately.
     */
    public static void updatePredicate() {

        PUMPKINS_PREDICATE = (state) -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN) || state.getBlock() instanceof CarvedPumpkinBlockCoFH);
    }

    public CarvedPumpkinBlockCoFH(Properties properties) {

        super(properties);
    }

    public CarvedPumpkinBlockCoFH setCarvePrev(Supplier<Block> carvePrev) {

        this.carvePrev = carvePrev;
        return this;
    }

    public CarvedPumpkinBlockCoFH setCarveNext(Supplier<Block> carveNext) {

        this.carveNext = carveNext;
        return this;
    }

    @Override
    public String getDescriptionId() {

        String specificTranslation = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
        if (ProxyUtils.canLocalize(specificTranslation)) {
            return specificTranslation;
        }
        return translationKey;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        ItemStack itemstack = player.getItemInHand(handIn);
        if (itemstack.getItem() instanceof ShearsItem) {
            if (!worldIn.isClientSide) {
                Direction direction = hit.getDirection();
                Direction direction1 = direction.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : direction;
                worldIn.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                worldIn.setBlock(pos, (player.isSecondaryUseActive() ? carvePrev.get() : carveNext.get())
                        .defaultBlockState()
                        .setValue(CarvedPumpkinBlock.FACING, direction1), 11);
                //                itemstack.damageItem(1, player, (entity) -> {
                //                    entity.sendBreakAnimation(handIn);
                //                });
            }
            return ActionResultType.SUCCESS;
        } else {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
    }

}
