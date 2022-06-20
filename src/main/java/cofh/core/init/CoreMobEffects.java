package cofh.core.init;

import cofh.core.content.effect.*;
import cofh.lib.content.effect.MobEffectCoFH;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.RegistryObject;

import static cofh.core.CoFHCore.MOB_EFFECTS;
import static cofh.core.util.references.CoreIDs.*;
import static cofh.lib.util.Constants.*;

public class CoreMobEffects {

    private CoreMobEffects() {

    }

    public static void register() {

        EXPLOSION_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_EXPLOSION_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x0F0A18));
        LIGHTNING_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_LIGHTNING_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0xA0A0A0));
        MAGIC_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_MAGIC_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x580058));
        SUPERCHARGE = MOB_EFFECTS.register(ID_EFFECT_SUPERCHARGE, () -> new EnergyChargeMobEffect(MobEffectCategory.BENEFICIAL, 0xCC1FFF, Integer.MAX_VALUE));
        CLARITY = MOB_EFFECTS.register(ID_EFFECT_CLARITY, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x70FF00));
        CHILLED = MOB_EFFECTS.register(ID_EFFECT_CHILLED, () -> new ChilledMobEffect(MobEffectCategory.HARMFUL, 0x86AEFD).addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID_EFFECT_CHILLED_MOVEMENT_SPEED.toString(), -0.30D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, UUID_EFFECT_CHILLED_ATTACK_SPEED.toString(), -0.40D, AttributeModifier.Operation.ADDITION));
        ENDERFERENCE = MOB_EFFECTS.register(ID_EFFECT_ENDERFERENCE, () -> new MobEffectCoFH(MobEffectCategory.NEUTRAL, 0x1B574D));
        LOVE = MOB_EFFECTS.register(ID_EFFECT_LOVE, () -> new LoveMobEffect(MobEffectCategory.BENEFICIAL, 0xFF7099));
        PANACEA = MOB_EFFECTS.register(ID_EFFECT_PANACEA, () -> new PanaceaMobEffect(MobEffectCategory.BENEFICIAL, 0x769CD7));
        SHOCKED = MOB_EFFECTS.register(ID_EFFECT_SHOCKED, () -> new MobEffectCoFH(MobEffectCategory.HARMFUL, 0xFFF4A5).addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_EFFECT_SHOCKED_ATTACK_DAMAGE.toString(), -3.0D, AttributeModifier.Operation.ADDITION));
        SLIMED = MOB_EFFECTS.register(ID_EFFECT_SLIMED, () -> new MobEffectCoFH(MobEffectCategory.NEUTRAL, 0x8CD782));
        SUNDERED = MOB_EFFECTS.register(ID_EFFECT_SUNDERED, () -> new MobEffectCoFH(MobEffectCategory.HARMFUL, 0x8C6A5C).addAttributeModifier(Attributes.ARMOR, UUID_EFFECT_SUNDERED_ARMOR.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ARMOR_TOUGHNESS, UUID_EFFECT_SUNDERED_ARMOR_TOUGHNESS.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        WRENCHED = MOB_EFFECTS.register(ID_EFFECT_WRENCHED, () -> new WrenchedMobEffect(MobEffectCategory.HARMFUL, 0xFF900A));

        // EFFECTS.register(ID_EFFECT_REDERGIZED, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x769CD7));
    }

    public static RegistryObject<MobEffect> EXPLOSION_RESISTANCE;
    public static RegistryObject<MobEffect> LIGHTNING_RESISTANCE;
    public static RegistryObject<MobEffect> MAGIC_RESISTANCE;
    public static RegistryObject<MobEffect> SUPERCHARGE;
    public static RegistryObject<MobEffect> CLARITY;
    public static RegistryObject<MobEffect> CHILLED;
    public static RegistryObject<MobEffect> ENDERFERENCE;
    public static RegistryObject<MobEffect> LOVE;
    public static RegistryObject<MobEffect> PANACEA;
    public static RegistryObject<MobEffect> SHOCKED;
    public static RegistryObject<MobEffect> SLIMED;
    public static RegistryObject<MobEffect> SUNDERED;
    public static RegistryObject<MobEffect> WRENCHED;

}
