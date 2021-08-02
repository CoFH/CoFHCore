package cofh.core.item;

import cofh.core.util.ProxyUtils;
import cofh.lib.item.IColorableItem;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.TAG_ENTITY;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public class SpawnEggItemCoFH extends SpawnEggItem implements IColorableItem {

    protected BooleanSupplier showInGroups = TRUE;

    private final int primaryColor;
    private final int secondaryColor;
    private final Supplier<EntityType<?>> typeSup;

    public SpawnEggItemCoFH(Supplier<EntityType<?>> typeSupIn, int primaryColorIn, int secondaryColorIn, Properties builder) {

        super(typeSupIn.get(), primaryColorIn, secondaryColorIn, builder);

        this.typeSup = typeSupIn;
        this.primaryColor = primaryColorIn;
        this.secondaryColor = secondaryColorIn;

        ProxyUtils.registerColorable(this);
    }

    public SpawnEggItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemGroup(group, items);
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {

        return tintIndex == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT tag) {

        if (tag != null && tag.contains(TAG_ENTITY, TAG_COMPOUND)) {
            CompoundNBT compoundnbt = tag.getCompound(TAG_ENTITY);
            if (compoundnbt.contains("id", 8)) {
                return EntityType.byKey(compoundnbt.getString("id")).orElse(this.typeSup.get());
            }
        }
        return this.typeSup.get();
    }

}
