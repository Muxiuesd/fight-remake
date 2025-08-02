package ttk.muxiuesd.registry;

import ttk.muxiuesd.serialization.*;
import ttk.muxiuesd.serialization.abs.EntityCodec;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 所有编解码器
 * */
public final class Codecs {
    public static final JsonCodec<Block> BLOCK = new BlockCodec();
    public static final JsonCodec<Block.Property> BLOCK_PROPERTY = new BlockPropertyCodec();
    public static final JsonCodec<Wall<?>> WALL = new WallCodec();
    public static final JsonCodec<Chunk> CHUNK = new ChunkCodec();

    public static final JsonCodec<Backpack> BACKPACK = new BackpackCodec();
    public static final JsonCodec<ItemStack> ITEM_STACK = new ItemStackCodec();
    public static final JsonCodec<Item.Property> ITEM_PROPERTY = new ItemPropertyCodec();

    public static final JsonCodec<Entity<?>> ENTITY = new EntityCodec();
    public static final JsonCodec<Entity.Property> ENTITY_PROPERTY = new EntityPropertyCodec();
}
