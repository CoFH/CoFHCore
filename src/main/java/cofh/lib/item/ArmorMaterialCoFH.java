package cofh.lib.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class ArmorMaterialCoFH implements IArmorMaterial {

    protected static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    protected final String name;
    protected final int maxDamageFactor;
    protected final int[] damageReductionAmountArray;
    protected final int enchantability;
    protected final SoundEvent soundEvent;
    protected final float toughness;
    protected final float knockbackResistance;
    protected final LazyValue<Ingredient> repairMaterial;

    public ArmorMaterialCoFH(String nameIn, int maxDamageFactorIn, int[] damageReductionAmountsIn, int enchantabilityIn, SoundEvent equipSoundIn, float toughnessIn, float knockbackResistanceIn, Supplier<Ingredient> repairMaterialSupplier) {

        this.name = nameIn;
        this.maxDamageFactor = maxDamageFactorIn;
        this.damageReductionAmountArray = damageReductionAmountsIn;
        this.enchantability = enchantabilityIn;
        this.soundEvent = equipSoundIn;
        this.toughness = toughnessIn;
        this.knockbackResistance = knockbackResistanceIn;
        this.repairMaterial = new LazyValue<>(repairMaterialSupplier);
    }

    // region IArmorMaterial
    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn) {

        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn) {

        return this.damageReductionAmountArray[slotIn.getIndex()];
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
    @OnlyIn (Dist.CLIENT)
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
