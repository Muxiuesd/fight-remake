package ttk.muxiuesd.registrant;

import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.lang.LangPack;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.recipe.CraftingTableRecipe;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.world.block.BlockSounds;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.loottable.block.BlockDropLootTable;
import ttk.muxiuesd.world.loottable.entity.EntityDeathLootTable;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 注册键
 * */
public class RegistryKeys {

    public static final RegistryKey<Codec<?>> CODEC = new RegistryKey<>();

    public static final RegistryKey<Item> ITEM = new RegistryKey<>();
    public static final RegistryKey<ItemGroup> ITEM_GROUP = new RegistryKey<>();
    public static final RegistryKey<Block> BLOCK = new RegistryKey<>();
    public static final RegistryKey<BlockEntityProvider<?>> BLOCK_ENTITY = new RegistryKey<>();
    public static final RegistryKey<Wall<?>> WALL = new RegistryKey<>();
    public static final RegistryKey<EntityType<?>> ENTITY_TYPE = new RegistryKey<>();
    public static final RegistryKey<EntityProvider<?>> ENTITY = new RegistryKey<>();
    public static final RegistryKey<DamageType<?, ?>> DAMAGE_TYPE = new RegistryKey<>();
    public static final RegistryKey<PropertyType<?>> PROPERTY_TYPE = new RegistryKey<>();
    public static final RegistryKey<StatusEffect> STATUS_EFFECT = new RegistryKey<>();

    public static final RegistryKey<IItemStackBehaviour> ITEM_STACK_BEHAVIOUR = new RegistryKey<>();
    public static final RegistryKey<CookingRecipe> COOKING_RECIPE = new RegistryKey<>();

    public static final RegistryKey<AudioHolder> AUDIOS = new RegistryKey<>();
    public static final RegistryKey<BlockSounds> BLOCK_SOUNDS = new RegistryKey<>();
    public static final RegistryKey<RenderLayer> RENDER_LAYER = new RegistryKey<>();

    public static final RegistryKey<BlockRenderer<? extends Block>> BLOCK_RENDERER = new RegistryKey<>();
    public static final RegistryKey<BlockEntityRenderer<? extends BlockEntity>> BLOCK_ENTITY_RENDERER = new RegistryKey<>();
    public static final RegistryKey<EntityRenderer<? extends Entity<?>>> ENTITY_RENDERER = new RegistryKey<>();

    public static final RegistryKey<CraftingTableRecipe> CRAFTING_RECIPE = new RegistryKey<>();

    public static final RegistryKey<BlockDropLootTable> BLOCK_DROP_LOOT_TABLE = new RegistryKey<>();
    public static final RegistryKey<EntityDeathLootTable> ENTITY_DEATH_LOOT_TABLE = new RegistryKey<>();

    public static final RegistryKey<ParticleEmitter<?>> PARTICLE_EMITTER = new RegistryKey<>();


    public static final RegistryKey<FightPool<?>> POOL = new RegistryKey<>();
    public static final RegistryKey<WorldInfoHashMap<?, ?>> WORLD_INFO_HASH_MAP = new RegistryKey<>();
    public static final RegistryKey<LangPack> LANG_HOLDER = new RegistryKey<>();

}
