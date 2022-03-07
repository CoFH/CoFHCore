package cofh.core.init;

import cofh.core.potion.*;
import cofh.lib.potion.EffectCoFH;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static cofh.core.CoFHCore.EFFECTS;
import static cofh.lib.util.constants.Constants.*;
import static cofh.lib.util.references.CoreIDs.*;

public class CoreEffects {

    private CoreEffects() {

    }

    public static void register() {

        EFFECTS.register(ID_EFFECT_EXPLOSION_RESISTANCE, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x0F0A18));
        EFFECTS.register(ID_EFFECT_LIGHTNING_RESISTANCE, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0xA0A0A0));
        EFFECTS.register(ID_EFFECT_MAGIC_RESISTANCE, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x580058));

        EFFECTS.register(ID_EFFECT_SUPERCHARGE, () -> new EnergyChargeEffect(MobEffectCategory.BENEFICIAL, 0xCC1FFF, Integer.MAX_VALUE));

        EFFECTS.register(ID_EFFECT_CLARITY, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x70FF00));
        EFFECTS.register(ID_EFFECT_CHILLED, () -> new ChilledEffect(MobEffectCategory.HARMFUL, 0x86AEFD)
                .addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID_EFFECT_CHILLED_MOVEMENT_SPEED.toString(), -0.30D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttributeModifier(Attributes.ATTACK_SPEED, UUID_EFFECT_CHILLED_ATTACK_SPEED.toString(), -0.40D, AttributeModifier.Operation.ADDITION));
<<<<<<< HEAD
        EFFECTS.register(ID_EFFECT_ENDERFERENCE, () -> new EffectCoFH(EffectType.NEUTRAL, 0x1B574D));
        EFFECTS.register(ID_EFFECT_LOVE, () -> new LoveEffect(EffectType.BENEFICIAL, 0xFF7099));
        EFFECTS.register(ID_EFFECT_PANACEA, () -> new PanaceaEffect(EffectType.BENEFICIAL, 0x769CD7));
        // EFFECTS.register(ID_EFFECT_REDERGIZED, () -> new EffectCoFH(EffectType.BENEFICIAL, 0x769CD7));
        EFFECTS.register(ID_EFFECT_SHOCKED, () -> new ShockedEffect(EffectType.HARMFUL, 0xFFF4A5)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_EFFECT_SHOCKED_ATTACK_DAMAGE.toString(), -3.0D, AttributeModifier.Operation.ADDITION));
        EFFECTS.register(ID_EFFECT_SLIMED, () -> new SlimedEffect(EffectType.NEUTRAL, 0x8CD782));
        EFFECTS.register(ID_EFFECT_SUNDERED, () -> new EffectCoFH(EffectType.HARMFUL, 0x8C6A5C)
=======
        EFFECTS.register(ID_EFFECT_ENDERFERENCE, () -> new EffectCoFH(MobEffectCategory.NEUTRAL, 0x1B574D));
        EFFECTS.register(ID_EFFECT_LOVE, () -> new LoveEffect(MobEffectCategory.BENEFICIAL, 0xFF7099));
        EFFECTS.register(ID_EFFECT_PANACEA, () -> new PanaceaEffect(MobEffectCategory.BENEFICIAL, 0x769CD7));
        // EFFECTS.register(ID_EFFECT_REDERGIZED, () -> new EffectCoFH(MobEffectCategory.BENEFICIAL, 0x769CD7));
        EFFECTS.register(ID_EFFECT_SHOCKED, () -> new EffectCoFH(MobEffectCategory.HARMFUL, 0xFFF4A5)
                .addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID_EFFECT_SHOCKED_ATTACK_DAMAGE.toString(), -3.0D, AttributeModifier.Operation.ADDITION));
        EFFECTS.register(ID_EFFECT_SLIMED, () -> new EffectCoFH(MobEffectCategory.NEUTRAL, 0x8CD782));
        EFFECTS.register(ID_EFFECT_SUNDERED, () -> new EffectCoFH(MobEffectCategory.HARMFUL, 0x8C6A5C)
>>>>>>> caa1a35 (Initial 1.18.2 compile pass.)
                .addAttributeModifier(Attributes.ARMOR, UUID_EFFECT_SUNDERED_ARMOR.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, UUID_EFFECT_SUNDERED_ARMOR_TOUGHNESS.toString(), -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        EFFECTS.register(ID_EFFECT_WRENCHED, () -> new WrenchedEffect(MobEffectCategory.HARMFUL, 0xFF900A));
    }

}
