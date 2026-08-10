package ttk.muxiuesd.registrant;

import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.Registry;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.lang.LangPack;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.world.block.BlockSounds;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 游戏内的所有注册表
 * */
public class Registries {
    public static final HashMap<RegistryKey<?>, Registry<?>> ALL_REGISTRY = new HashMap<>();

    public static final Registry<Codec<?>> CODEC = create(RegistryKeys.CODEC);

    public static final Registry<Item> ITEM = create(RegistryKeys.ITEM);
    public static final Registry<ItemGroup> ITEM_GROUP = create(RegistryKeys.ITEM_GROUP);
    public static final Registry<Block> BLOCK = create(RegistryKeys.BLOCK);
    public static final Registry<BlockEntityProvider<?>> BLOCK_ENTITY = create(RegistryKeys.BLOCK_ENTITY);
    public static final Registry<Wall<?>> WALL = create(RegistryKeys.WALL);
    public static final Registry<EntityType<?>> ENTITY_TYPE = create(RegistryKeys.ENTITY_TYPE);
    public static final Registry<EntityProvider<?>> ENTITY = create(RegistryKeys.ENTITY);
    public static final Registry<DamageType<?, ?>> DAMAGE_TYPE = create(RegistryKeys.DAMAGE_TYPE);
    public static final Registry<PropertyType<?>> PROPERTY_TYPE = create(RegistryKeys.PROPERTY_TYPE);
    public static final Registry<StatusEffect> STATUS_EFFECT = create(RegistryKeys.STATUS_EFFECT);

    public static final Registry<IItemStackBehaviour> ITEM_STACK_BEHAVIOUR = create(RegistryKeys.ITEM_STACK_BEHAVIOUR);
    public static final Registry<CookingRecipe> COOKING_RECIPE = create(RegistryKeys.COOKING_RECIPE);

    public static final Registry<AudioHolder> AUDIOS = create(RegistryKeys.AUDIOS);
    public static final Registry<BlockSounds> BLOCK_SOUNDS = create(RegistryKeys.BLOCK_SOUNDS);
    public static final Registry<RenderLayer> RENDER_LAYER = create(RegistryKeys.RENDER_LAYER);

    public static final CraftingRecipeRegistry CRAFTING_RECIPE_REGISTRY = create(RegistryKeys.CRAFTING_RECIPE_REGISTRY_KEY, new CraftingRecipeRegistry());



    public static final BlockRendererRegistry BLOCK_RENDERER = create(
        RegistryKeys.BLOCK_RENDERER,
        BlockRendererRegistry.getInstance()
    );
    public static final BlockEntityRendererRegistry BLOCK_ENTITY_RENDERER = create(
        RegistryKeys.BLOCK_ENTITY_RENDERER,
        BlockEntityRendererRegistry.getInstance()
    );

    public static final EntityRendererRegistry ENTITY_RENDERER = create(
        RegistryKeys.ENTITY_RENDERER,
        EntityRendererRegistry.getInstance()
    );

    public static final Registry<FightPool<?>> POOL = create(RegistryKeys.POOL);
    public static final Registry<WorldInfoHashMap<?, ?>> WORLD_INFO_HASH_MAP = create(RegistryKeys.WORLD_INFO_HASH_MAP);
    public static final Registry<LangPack> LANG_HOLDER = create(RegistryKeys.LANG_HOLDER);


    /**
     * 创建一个默认的注册表
     * */
    public static <T> DefaultRegistry<T> create (RegistryKey<T> registryKey) {
        DefaultRegistry<T> registry = new DefaultRegistry<>();
        ALL_REGISTRY.put(registryKey, registry);
        return registry;
    }

    /**
     * 创建一个自定义的注册表
     * */
    public static <T, R extends Registry<T>> R create (RegistryKey<T> registryKey, R registry) {
        ALL_REGISTRY.put(registryKey, registry);
        return registry;
    }

    /**
     * 默认的注册表实现类
     * */
    public static class DefaultRegistry<T> implements Registry<T> {
        private final LinkedHashMap<String, Identifier> idMap = new LinkedHashMap<>();
        private final LinkedHashMap<Identifier, T> regedit = new LinkedHashMap<>();


        @Override
        public T register (Identifier identifier, T value) {
            if (this.contains(identifier.getID()) || this.contains(identifier)) {
                throw new RuntimeException("注册Id：" + identifier.getID() + " 重复！！！");
            }
            this.idMap.put(identifier.getID(), identifier);
            this.regedit.put(identifier, value);
            return value;
        }

        @Override
        public T get (Identifier identifier) {
            if (identifier == null) {
                throw new RuntimeException();
            }
            if (!this.contains(identifier)) {
                throw new RuntimeException("注册Id：" + identifier.getID() + " 不存在！！！");
            }
            return this.regedit.get(identifier);
        }

        @Override
        public T get (String id) {
            if (!this.contains(id)) {
                throw new RuntimeException("注册Id：" + id + " 不存在！！！");
            }
            return this.regedit.get(this.idMap.get(id));
        }

        /**
         * 安全获取：id 未注册时返回 null 而不是抛异常
         * <p>
         * 用于存档解码等容错路径（未知 id 的旧数据应跳过而不是崩溃/整块重建）
         * */
        public T getOrNull (String id) {
            Identifier identifier = this.idMap.get(id);
            if (identifier == null) return null;
            return this.regedit.get(identifier);
        }

        @Override
        public boolean contains (String id) {
            return this.idMap.containsKey(id);
        }

        @Override
        public boolean contains (Identifier identifier) {
            return this.regedit.containsKey(identifier);
        }

        @Override
        public HashMap<Identifier, T> getMap () {
            return this.regedit;
        }

        public HashMap<String, Identifier> getIDCast () {
            return this.idMap;
        }
    }
}
