package ttk.muxiuesd.registrant;

import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.damage.DamageType;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 注册键
 * */
public class RegistryKeys {
    public static final RegistryKey<Item> ITEM = new RegistryKey<>();
    public static final RegistryKey<Block> BLOCK = new RegistryKey<>();
    public static final RegistryKey<EntityType<?>> ENTITY_TYPE = new RegistryKey<>();
    public static final RegistryKey<Supplier<? extends Entity<?>>> ENTITY = new RegistryKey<>();
    public static final RegistryKey<DamageType<?, ?>> DAMAGE_TYPE = new RegistryKey<>();
    public static final RegistryKey<PropertyType<?>> PROPERTY_TYPE = new RegistryKey<>();

    public static final RegistryKey<IItemStackBehaviour> ITEM_STACK_BEHAVIOUR = new RegistryKey<>();
    public static final RegistryKey<CookingRecipe> COOKING_RECIPE = new RegistryKey<>();

    public static final RegistryKey<Audio> AUDIOS = new RegistryKey<>();
    public static final RegistryKey<BlockSoundsID> BLOCK_SOUNDS = new RegistryKey<>();
    public static final RegistryKey<RenderLayer> RENDER_LAYER = new RegistryKey<>();

    public static final RegistryKey<FightPool<?>> POOL = new RegistryKey<>();
}
