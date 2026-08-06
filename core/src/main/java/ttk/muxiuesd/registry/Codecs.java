package ttk.muxiuesd.registry;

import ttk.muxiuesd.serialization.*;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.Botany;
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
 * <p>
 * @deprecated 已弃用：这是旧式编解码器（JSON 树格式）的注册表，已被各对象上的新式编解码器（
 * {@link game.muxiuesd.bedrockcore.serialization.Codec}，RawObject 格式）替代。
 * 目前仅保留供内部互相引用，不再注册到 {@link ttk.muxiuesd.registrant.Registries#CODEC}
 * */
@Deprecated
public final class Codecs {
    /// 方块、墙体、区块相关的编解码器
    public static final JsonCodec<Block> BLOCK = new BlockCodec();
    public static final JsonCodec<BlockEntity> BLOCK_ENTITY = new BlockEntityCodec();
    public static final JsonCodec<Block.Property> BLOCK_PROPERTY = new BlockPropertyCodec();
    public static final JsonCodec<Wall<?>> WALL = new WallCodec();
    public static final JsonCodec<Botany> BOTANY = new BotanyCodec();
    public static final JsonCodec<Chunk> CHUNK = new ChunkCodec();

    /// 物品相关的编解码器
    public static final JsonCodec<Backpack> BACKPACK = new BackpackCodec();
    public static final JsonCodec<ItemStack> ITEM_STACK = new ItemStackCodec();
    public static final JsonCodec<Item.Property> ITEM_PROPERTY = new ItemPropertyCodec();

    /// 实体相关的编解码器
    public static final JsonCodec<Entity<?>> ENTITY = new EntityCodec();
    public static final JsonCodec<LivingEntity<?>> LIVING_ENTITY = new LivingEntityCodec();
    public static final JsonCodec<Player> PLAYER = new PlayerCodec();
    public static final JsonCodec<ItemEntity> ITEM_ENTITY = new ItemEntityCodec();
    public static final JsonCodec<Entity.Property> ENTITY_PROPERTY = new EntityPropertyCodec();
    public static final JsonCodec<LinkedHashMap<StatusEffect, StatusEffect.Data>> STATUS_EFFECTS = new BuffCodec();
}
