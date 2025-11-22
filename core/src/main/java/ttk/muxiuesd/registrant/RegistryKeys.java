package ttk.muxiuesd.registrant;

import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.interfaces.serialization.Codec;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.lang.LangPack;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 注册键
 * */
public class RegistryKeys {

    public static final RegistryKey<Codec<?, ?, ?>> CODEC = new RegistryKey<>();

    public static final RegistryKey<Item> ITEM = new RegistryKey<>();
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

    public static final RegistryKey<Audio> AUDIOS = new RegistryKey<>();
    public static final RegistryKey<BlockSoundsID> BLOCK_SOUNDS = new RegistryKey<>();
    public static final RegistryKey<RenderLayer> RENDER_LAYER = new RegistryKey<>();

    public static final RegistryKey<BlockRenderer<? extends Block>> BLOCK_RENDERER = new RegistryKey<>();
    public static final RegistryKey<BlockEntityRenderer<? extends BlockEntity>> BLOCK_ENTITY_RENDERER = new RegistryKey<>();

    public static final RegistryKey<FightPool<?>> POOL = new RegistryKey<>();
    public static final RegistryKey<WorldInfoHashMap<?, ?>> WORLD_INFO_HASH_MAP = new RegistryKey<>();
    public static final RegistryKey<LangPack> LANG_HOLDER = new RegistryKey<>();

}
