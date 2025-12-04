package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.serialization.Codec;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.*;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

import java.util.LinkedHashMap;

/**
 * 所有编解码器的注册
 * */
public final class Codecs {
    /// 方块、墙体、区块相关的编解码器
    public static final JsonCodec<Block> BLOCK = register("block", new BlockCodec());
    public static final JsonCodec<BlockEntity> BLOCK_ENTITY = register("block_entity", new BlockEntityCodec());
    public static final JsonCodec<Block.Property> BLOCK_PROPERTY = register("block_property", new BlockPropertyCodec());
    public static final JsonCodec<Wall<?>> WALL = register("wall", new WallCodec());
    public static final JsonCodec<Chunk> CHUNK = register("chunk", new ChunkCodec());

    /// 物品相关的编解码器
    public static final JsonCodec<Backpack> BACKPACK = register("backpack", new BackpackCodec());
    public static final JsonCodec<ItemStack> ITEM_STACK = register("item_stack", new ItemStackCodec());
    public static final JsonCodec<Item.Property> ITEM_PROPERTY = register("item_property", new ItemPropertyCodec());

    /// 实体相关的编解码器
    public static final JsonCodec<Entity<?>> ENTITY = register("entity", new EntityCodec());
    public static final JsonCodec<LivingEntity<?>> LIVING_ENTITY = register("living_entity", new LivingEntityCodec());
    public static final JsonCodec<Player> PLAYER = register("player", new PlayerCodec());
    public static final JsonCodec<ItemEntity> ITEM_ENTITY = register("item_entity", new ItemEntityCodec());
    public static final JsonCodec<Entity.Property> ENTITY_PROPERTY = register("item_property", new EntityPropertyCodec());
    public static final JsonCodec<LinkedHashMap<StatusEffect, StatusEffect.Data>> STATUS_EFFECTS = register("status_effect", new BuffCodec());


    /**
     * 注册一种编解码器
     * */
    public static <C extends Codec<?, ?, ?>> C register (String name, C codec) {
        Registries.CODEC.register(new Identifier(Fight.ID(name)), codec);
        return codec;
    }
}
