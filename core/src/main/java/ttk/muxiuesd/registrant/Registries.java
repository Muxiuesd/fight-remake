package ttk.muxiuesd.registrant;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.audio.Audio;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.Registry;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.damage.DamageType;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 游戏内的所有注册表
 * */
public class Registries {
    public static final Array<Registry<?>> ALL_REGISTRY = new Array<>();

    public static final Registry<Item> ITEM = create(RegistryKeys.ITEM);
    public static final Registry<Block> BLOCK = create(RegistryKeys.BLOCK);
    public static final Registry<BlockEntityProvider<?>> BLOCK_ENTITY = create(RegistryKeys.BLOCK_ENTITY);
    public static final Registry<Wall<?>> WALL = create(RegistryKeys.WALL);
    public static final Registry<EntityType<?>> ENTITY_TYPE = create(RegistryKeys.ENTITY_TYPE);
    public static final Registry<EntityProvider<?>> ENTITY = create(RegistryKeys.ENTITY);
    public static final Registry<DamageType<?, ?>> DAMAGE_TYPE = create(RegistryKeys.DAMAGE_TYPE);
    public static final Registry<PropertyType<?>> PROPERTY_TYPE = create(RegistryKeys.PROPERTY_TYPE);

    public static final Registry<IItemStackBehaviour> ITEM_STACK_BEHAVIOUR = create(RegistryKeys.ITEM_STACK_BEHAVIOUR);
    public static final Registry<CookingRecipe> COOKING_RECIPE = create(RegistryKeys.COOKING_RECIPE);

    public static final Registry<Audio> AUDIOS = create(RegistryKeys.AUDIOS);
    public static final Registry<BlockSoundsID> BLOCK_SOUNDS = create(RegistryKeys.BLOCK_SOUNDS);
    public static final Registry<RenderLayer> RENDER_LAYER = create(RegistryKeys.RENDER_LAYER);

    public static final Registry<FightPool<?>> POOL = create(RegistryKeys.POOL);
    public static final Registry<WorldInfoHashMap<?, ?>> WORLD_INFO_HASH_MAP = create(RegistryKeys.WORLD_INFO_HASH_MAP);

    /**
     * 创建一个注册表
     * */
    public static <T> DefaultRegistry<T> create (RegistryKey<T> registryKey) {
        DefaultRegistry<T> registry = new DefaultRegistry<>();
        ALL_REGISTRY.add(registry);
        return registry;
    }

    /**
     * 默认的注册表
     * */
    public static class DefaultRegistry<T> implements Registry<T> {
        private final LinkedHashMap<String, Identifier> idMap = new LinkedHashMap<>();
        private final LinkedHashMap<Identifier, T> regedit = new LinkedHashMap<>();


        @Override
        public T register (Identifier identifier, T value) {
            if (this.contains(identifier.getId()) || this.contains(identifier)) {
                Identifier oldId = this.idMap.get(identifier.getId());
                T oldValue = this.regedit.get(oldId);
                Log.print(this.getClass().getName(),
                    "覆盖旧的注册元素：" + identifier.getId() + "@@" + oldValue
                    + " 新的值为：" + value);
            }
            this.idMap.put(identifier.getId(), identifier);
            this.regedit.put(identifier, value);
            return value;
        }

        @Override
        public T get (Identifier identifier) {
            if (identifier == null) {
                throw new RuntimeException();
            }
            if (!this.contains(identifier)) {
                throw new RuntimeException("注册Id：" + identifier.getId() + " 不存在！！！");
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

        @Override
        public HashMap<Identifier, T> getMap () {
            return this.regedit;
        }

        @Override
        public boolean contains (String id) {
            return this.idMap.containsKey(id);
        }

        @Override
        public boolean contains (Identifier identifier) {
            return this.regedit.containsKey(identifier);
        }
    }
}
