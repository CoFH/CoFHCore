package cofh.lib.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public class ArmorMaterialCoFH implements ArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    protected final String name;
    protected final int maxDamageFactor;
    protected final int[] damageReductionAmountArray;
    protected final int enchantability;
    protected final SoundEvent soundEvent;
    protected final float toughness;
    protected final float knockbackResistance;
    protected final LazyLoadedValue<Ingredient> repairMaterial;

    public ArmorMaterialCoFH(String nameIn, int maxDamageFactorIn, int[] damageReductionAmountsIn, int enchantabilityIn, SoundEvent equipSoundIn, float toughnessIn, float knockbackResistanceIn, Supplier<Ingredient> repairMaterialSupplier) {

        this.name = nameIn;
        this.maxDamageFactor = maxDamageFactorIn;
        this.damageReductionAmountArray = damageReductionAmountsIn;
        this.enchantability = enchantabilityIn;
        this.soundEvent = equipSoundIn;
        this.toughness = toughnessIn;
        this.knockbackResistance = knockbackResistanceIn;
        this.repairMaterial = new LazyLoadedValue<>(repairMaterialSupplier);
    }

    // region ArmorMaterial
    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {

        return MAX_DAMAGE_ARRAY[pType.ordinal()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {

        return this.damageReductionAmountArray[pType.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {

        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {

        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {

        return this.repairMaterial.get();
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public float getToughness() {

        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {

        return this.knockbackResistance;
    }
    // endregion
}
