package cofh.lib.item.impl;

import cofh.core.util.ProxyUtils;
import cofh.lib.item.IColorableItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static cofh.lib.util.constants.Constants.TRUE;
import static cofh.lib.util.constants.NBTTags.TAG_ENTITY;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

// TODO extend ForgeSpawnEggItem?
public class SpawnEggItemCoFH extends SpawnEggItem implements IColorableItem {

    private static final Set<SpawnEggItemCoFH> EGG_ITEMS = new ObjectOpenHashSet<>();

    protected BooleanSupplier showInGroups = TRUE;

    private final int primaryColor;
    private final int secondaryColor;
    private final Supplier<EntityType<? extends Mob>> typeSup;

    public SpawnEggItemCoFH(Supplier<EntityType<? extends Mob>> typeSupIn, int primaryColorIn, int secondaryColorIn, Properties builder) {

        super(typeSupIn.get(), primaryColorIn, secondaryColorIn, builder);

        this.typeSup = typeSupIn;
        this.primaryColor = primaryColorIn;
        this.secondaryColor = secondaryColorIn;

        ProxyUtils.registerColorable(this);

        BY_ID.remove(typeSupIn.get());
        EGG_ITEMS.add(this);
    }

    public static void setup() {

        for (SpawnEggItemCoFH egg : EGG_ITEMS) {
            BY_ID.put(egg.typeSup.get(), egg);
        }
    }

    public SpawnEggItemCoFH setShowInGroups(BooleanSupplier showInGroups) {

        this.showInGroups = showInGroups;
        return this;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {

        if (!showInGroups.getAsBoolean()) {
            return;
        }
        super.fillItemCategory(group, items);
    }

    @OnlyIn (Dist.CLIENT)
    public int getColor(ItemStack item, int colorIndex) {

        return colorIndex == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag) {

        if (tag != null && tag.contains(TAG_ENTITY, TAG_COMPOUND)) {
            CompoundTag compoundnbt = tag.getCompound(TAG_ENTITY);
            if (compoundnbt.contains("id", 8)) {
                return EntityType.byString(compoundnbt.getString("id")).orElse(this.typeSup.get());
            }
        }
        return this.typeSup.get();
    }

}
