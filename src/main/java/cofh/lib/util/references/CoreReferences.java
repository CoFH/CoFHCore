package cofh.lib.util.references;

import cofh.core.inventory.container.HeldItemFilterContainer;
import cofh.core.inventory.container.TileItemFilterContainer;
import cofh.lib.entity.BlackHoleEntity;
import cofh.lib.entity.ElectricArcEntity;
import cofh.lib.entity.ElectricFieldEntity;
import cofh.lib.entity.KnifeEntity;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

import static cofh.lib.util.constants.Constants.ID_COFH_CORE;
import static cofh.lib.util.references.CoreIDs.*;

@ObjectHolder (ID_COFH_CORE)
public class CoreReferences {

    private CoreReferences() {

    }

    // region BLOCKS
    @ObjectHolder (ID_GLOSSED_MAGMA)
    public static final Block GLOSSED_MAGMA = null;

    @ObjectHolder (ID_SIGNAL_AIR)
    public static final Block SIGNAL_AIR = null;

    @ObjectHolder (ID_GLOW_AIR)
    public static final Block GLOW_AIR = null;

    @ObjectHolder (ID_ENDER_AIR)
    public static final Block ENDER_AIR = null;

    @ObjectHolder (ID_LIGHTNING_AIR)
    public static final Block LIGHTNING_AIR = null;
    // endregion

    // region CONTAINERS
    //    @ObjectHolder(ID_CONTAINER_HELD_FLUID_FILTER)
    //    public static final ContainerType<HeldFluidFilterContainer> HELD_FLUID_FILTER_CONTAINER = null;
    //
    //    @ObjectHolder(ID_CONTAINER_TILE_FLUID_FILTER)
    //    public static final ContainerType<TileFluidFilterContainer> TILE_FLUID_FILTER_CONTAINER = null;

    @ObjectHolder (ID_CONTAINER_HELD_ITEM_FILTER)
    public static final ContainerType<HeldItemFilterContainer> HELD_ITEM_FILTER_CONTAINER = null;

    @ObjectHolder (ID_CONTAINER_TILE_ITEM_FILTER)
    public static final ContainerType<TileItemFilterContainer> TILE_ITEM_FILTER_CONTAINER = null;
    // endregion

    // region FLUIDS
    @ObjectHolder (ID_FLUID_HONEY)
    public static final FlowingFluid FLUID_HONEY = null;

    @ObjectHolder (ID_FLUID_MILK)
    public static final FlowingFluid FLUID_MILK = null;

    @ObjectHolder (ID_FLUID_POTION)
    public static final FlowingFluid FLUID_POTION = null;

    @ObjectHolder (ID_FLUID_STEAM)
    public static final FlowingFluid FLUID_STEAM = null;

    @ObjectHolder (ID_FLUID_XP)
    public static final FlowingFluid FLUID_XP = null;
    // endregion

    // region TILES
    @ObjectHolder (ID_SIGNAL_AIR)
    public static final TileEntityType<?> SIGNAL_AIR_TILE = null;

    @ObjectHolder (ID_GLOW_AIR)
    public static final TileEntityType<?> GLOW_AIR_TILE = null;

    @ObjectHolder (ID_ENDER_AIR)
    public static final TileEntityType<?> ENDER_AIR_TILE = null;

    @ObjectHolder (ID_LIGHTNING_AIR)
    public static final TileEntityType<?> LIGHTNING_AIR_TILE = null;
    // endregion

    // region EFFECTS
    @ObjectHolder (ID_EFFECT_EXPLOSION_RESISTANCE)
    public static final Effect EXPLOSION_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_LIGHTNING_RESISTANCE)
    public static final Effect LIGHTNING_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_MAGIC_RESISTANCE)
    public static final Effect MAGIC_RESISTANCE = null;

    @ObjectHolder (ID_EFFECT_AMPLIFICATION)
    public static final Effect AMPLIFICATION = null;

    @ObjectHolder (ID_EFFECT_CHILLED)
    public static final Effect CHILLED = null;

    @ObjectHolder (ID_EFFECT_CLARITY)
    public static final Effect CLARITY = null;

    @ObjectHolder (ID_EFFECT_ENDERFERENCE)
    public static final Effect ENDERFERENCE = null;

    @ObjectHolder (ID_EFFECT_LOVE)
    public static final Effect LOVE = null;

    @ObjectHolder (ID_EFFECT_PANACEA)
    public static final Effect PANACEA = null;

    @ObjectHolder (ID_EFFECT_SHOCKED)
    public static final Effect SHOCKED = null;

    @ObjectHolder (ID_EFFECT_SLIMED)
    public static final Effect SLIMED = null;

    @ObjectHolder (ID_EFFECT_SUNDERED)
    public static final Effect SUNDERED = null;

    @ObjectHolder (ID_EFFECT_SUPERCHARGE)
    public static final Effect SUPERCHARGE = null;

    @ObjectHolder (ID_EFFECT_WRENCHED)
    public static final Effect WRENCHED = null;
    // endregion

    // region ENCHANTMENTS
    @ObjectHolder (ID_HOLDING)
    public static final Enchantment HOLDING = null;
    // endregion

    // region ITEMS
    @ObjectHolder (ID_ECTOPLASM)
    public static final Item ECTOPLASM = null;
    // endregion

    // region PARTICLES
    //@ObjectHolder(ID_PARTICLE_SNOW)
    //public static final BasicParticleType SNOW = null;

    @ObjectHolder (ID_PARTICLE_FROST)
    public static final BasicParticleType FROST_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_SPARK)
    public static final BasicParticleType SPARK_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_PLASMA)
    public static final BasicParticleType PLASMA_PARTICLE = null;

    @ObjectHolder (ID_PARTICLE_SHOCKWAVE)
    public static final BasicParticleType SHOCKWAVE_PARTICLE = null;

    @ObjectHolder(ID_PARTICLE_BLAST_WAVE)
    public static final BasicParticleType BLAST_WAVE_PARTICLE = null;

    @ObjectHolder(ID_PARTICLE_VORTEX)
    public static final BasicParticleType VORTEX_PARTICLE = null;

    @ObjectHolder(ID_PARTICLE_SPIRAL)
    public static final BasicParticleType SPIRAL_PARTICLE = null;
    // endregion

    // region ENTITIES
    @ObjectHolder (ID_KNIFE)
    public static final EntityType<KnifeEntity> KNIFE_ENTITY = null;

    @ObjectHolder (ID_ELECTRIC_ARC)
    public static final EntityType<ElectricArcEntity> ELECTRIC_ARC_ENTITY = null;

    @ObjectHolder (ID_ELECTRIC_FIELD)
    public static final EntityType<ElectricFieldEntity> ELECTRIC_FIELD_ENTITY = null;

    @ObjectHolder (ID_BLACK_HOLE)
    public static final EntityType<BlackHoleEntity> BLACK_HOLE_ENTITY = null;
    // endregion

    // region SOUND EVENTS
    //@ObjectHolder (ID_SOUND_ELECTRICITY)
    //public static final SoundEvent ELECTRICITY_SOUND = null;
    // endregion
}
