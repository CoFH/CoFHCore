package cofh.core.init;

import cofh.core.effect.*;
import cofh.lib.effect.MobEffectCoFH;
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

        // EFFECTS.register(ID_EFFECT_REDERGIZED, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x769CD7));
    }

    public static final RegistryObject<MobEffect> EXPLOSION_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_EXPLOSION_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x0F0A18));
    public static final RegistryObject<MobEffect> LIGHTNING_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_LIGHTNING_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0xA0A0A0));
    public static final RegistryObject<MobEffect> MAGIC_RESISTANCE = MOB_EFFECTS.register(ID_EFFECT_MAGIC_RESISTANCE, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x580058));

    public static final RegistryObject<MobEffect> SUPERCHARGE = MOB_EFFECTS.register(ID_EFFECT_SUPERCHARGE, () -> new EnergyChargeMobEffect(MobEffectCategory.BENEFICIAL, 0xCC1FFF, Integer.MAX_VALUE));

    public static final RegistryObject<MobEffect> CLARITY = MOB_EFFECTS.register(ID_EFFECT_CLARITY, () -> new MobEffectCoFH(MobEffectCategory.BENEFICIAL, 0x70FF00));
    public static final RegistryObject<MobEffect> LOVE = MOB_EFFECTS.register(ID_EFFECT_LOVE, () -> new LoveMobEffect(MobEffectCategory.BENEFICIAL, 0xFF7099));
    public static final RegistryObject<MobEffect> PANACEA = MOB_EFFECTS.register(ID_EFFECT_PANACEA, () -> new PanaceaMobEffect(MobEffectCategory.BENEFICIAL, 0x769CD7));

    public static final RegistryObject<MobEffect> ENDERFERENCE = MOB_EFFECTS.register(ID_EFFECT_ENDERFERENCE, () -> new MobEffectCoFH(MobEffectCategory.NEUTRAL, 0x1B574D));
    public static final RegistryObject<MobEffect> SLIMED = MOB_EFFECTS.register(ID_EFFECT_SLIMED, () -> new MobEffectCoFH(MobEffectCategory.NEUTRAL, 0x8CD782));

    public static final RegistryObject<MobEffect> CHILLED = MOB_EFFECTS.register(ID_EFFECT_CHILLED, () -> new ChilledMobEffect(MobEffectCategory.HARMFUL, 0x86AEFD).addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID_EFFECT_CHILLED_MOVEMENT_SPEED.toString(), -0.30D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ATTACK_SPEED, UUID_EFFECT_CHILLED_ATTACK_SPEED.toString(), -0.40D, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> SHOCKED = MOB_EFFECTS.register(ID_EFFECT_SHOCKED, () -> new ShockedMobEffect(MobEffectCategory.HARMFUL, 0xFFF4A5).addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_EFFECT_SHOCKED_ATTACK_DAMAGE.toString(), -3.0D, AttributeModifier.Operation.ADDITION));
    public static final RegistryObject<MobEffect> SUNDERED = MOB_EFFECTS.register(ID_EFFECT_SUNDERED, () -> new MobEffectCoFH(MobEffectCategory.HARMFUL, 0x8C6A5C).addAttributeModifier(Attributes.ARMOR, UUID_EFFECT_SUNDERED_ARMOR.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(Attributes.ARMOR_TOUGHNESS, UUID_EFFECT_SUNDERED_ARMOR_TOUGHNESS.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> WRENCHED = MOB_EFFECTS.register(ID_EFFECT_WRENCHED, () -> new WrenchedMobEffect(MobEffectCategory.HARMFUL, 0xFF900A));

}
