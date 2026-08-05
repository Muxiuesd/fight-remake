package ttk.muxiuesd.serialization.codecs;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.wall.Wall;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 区块的现代化编解码器
 * */
public class CodecChunk {
    /**
     * 区块的编解码器
     * <p>
     * 结构：<br>
     * blocks：每个坐标的方块（普通方块是id字符串，带方块实体的方块是完整数据map）<br>
     * walls：不为空的墙体<br>
     * botany：不为空的植物<br>
     * heights：每个坐标的高度
     * */
    public static final Codec<Chunk> CODEC = new Codec<>() {
        @Override
        public RawObject encode (Chunk chunk) {
            Map<String, Object> blocks = new LinkedHashMap<>();
            Map<String, Object> walls = new LinkedHashMap<>();
            Map<String, Object> botanys = new LinkedHashMap<>();
            Map<String, Object> heights = new LinkedHashMap<>();

            chunk.traversal((x, y) -> {
                String name = getCellName(x, y);

                Block block = chunk.getBlock(x, y);
                if (block != null) {
                    //普通方块是字符串id，带方块实体的方块是完整数据
                    Codec<Block> codec = (Codec<Block>) block.getCodec();
                    blocks.put(name, codec.encode(block).unwrap());
                }

                Wall<?> wall = chunk.getWall(x, y);
                if (wall != null) {
                    walls.put(name, Wall.CODEC.encode(wall).unwrap());
                }

                Botany botany = chunk.getBotany(x, y);
                if (botany != null) {
                    botanys.put(name, Botany.CODEC.encode(botany).unwrap());
                }

                heights.put(name, Codec.INT.encode(chunk.getHeight(x, y)).unwrap());
            });

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("blocks", blocks);
            root.put("walls", walls);
            root.put("botany", botanys);
            root.put("heights", heights);
            return RawObject.ofMap(root);
        }

        @Override
        public DataResult<Chunk> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");

            Chunk chunk = new Chunk();
            StringBuilder errors = new StringBuilder();

            Map<String, Object> rawMap = input.asMap().get();
            //方块
            Map<String, Object> blocks = asMap(Codec.wrap(rawMap.get("blocks")));
            //墙体
            Map<String, Object> walls = asMap(Codec.wrap(rawMap.get("walls")));
            //植物
            Map<String, Object> botanys = asMap(Codec.wrap(rawMap.get("botany")));
            //高度
            Map<String, Object> heights = asMap(Codec.wrap(rawMap.get("heights")));

            chunk.traversal((x, y) -> {
                String name = getCellName(x, y);

                //方块（解码异常时跳过该格，不中断整块区块）
                if (blocks.containsKey(name)) {
                    try {
                        DataResult<Block> blockResult = decodeBlock(Codec.wrap(blocks.get(name)));
                        if (blockResult.isSuccess()) {
                            chunk.setBlock(blockResult.result().get(), x, y);
                        } else {
                            errors.append("[").append(name).append("]方块: ").append(blockResult.error().orElse("")).append("; ");
                        }
                    } catch (Exception e) {
                        errors.append("[").append(name).append("]方块解码异常: ").append(e.getMessage()).append("; ");
                    }
                }

                //墙体（解码异常时跳过该格，不中断整块区块）
                if (walls.containsKey(name)) {
                    try {
                        DataResult<Wall<?>> wallResult = Wall.CODEC.decode(Codec.wrap(walls.get(name)));
                        if (wallResult.isSuccess()) {
                            chunk.setWall(wallResult.result().get(), x, y);
                        } else {
                            errors.append("[").append(name).append("]墙体: ").append(wallResult.error().orElse("")).append("; ");
                        }
                    } catch (Exception e) {
                        errors.append("[").append(name).append("]墙体解码异常: ").append(e.getMessage()).append("; ");
                    }
                }

                //植物（解码异常时跳过该格，不中断整块区块）
                if (botanys.containsKey(name)) {
                    try {
                        DataResult<Botany> botanyResult = Botany.CODEC.decode(Codec.wrap(botanys.get(name)));
                        if (botanyResult.isSuccess()) {
                            chunk.setBotany(botanyResult.result().get(), x, y);
                        } else {
                            errors.append("[").append(name).append("]植物: ").append(botanyResult.error().orElse("")).append("; ");
                        }
                    } catch (Exception e) {
                        errors.append("[").append(name).append("]植物解码异常: ").append(e.getMessage()).append("; ");
                    }
                }

                //高度
                if (heights.containsKey(name)) {
                    DataResult<Integer> heightResult = Codec.INT.decode(Codec.wrap(heights.get(name)));
                    if (heightResult.isSuccess()) {
                        int height = heightResult.result().get();
                        //高度有取值范围，防止存档数据越界
                        if (height < Chunk.LowestHeight) height = Chunk.LowestHeight;
                        if (height > Chunk.HighestHeight) height = Chunk.HighestHeight;
                        chunk.setHeight(x, y, height);
                    }
                }
            });

            if (errors.length() > 0) return DataResult.error(errors.toString(), chunk);
            return DataResult.success(chunk);
        }
    };

    /**
     * 解码一个方块单元格的数据
     * <p>
     * 普通方块是id字符串，带方块实体的方块是完整数据map
     * */
    private static DataResult<Block> decodeBlock (RawObject raw) {
        if (raw.isString()) {
            //普通方块（getOrNull：未知 id 返回 null 不抛异常，该格降级为空气方块）
            String id = raw.asString().get();
            Block block = Registries.BLOCK.getOrNull(id);
            if (block == null) return DataResult.error("不存在的方块：" + id);
            return DataResult.success(block);
        }
        if (raw.isMap()) {
            //带方块实体的方块
            String id = Codec.STRING.decode(Codec.wrap(raw.asMap().get().get("id"))).result().orElse(null);
            if (id == null) return DataResult.error("缺少方块id");
            Block prototype = Registries.BLOCK.getOrNull(id);
            if (prototype == null) return DataResult.error("不存在的方块：" + id);
            Codec<Block> codec = (Codec<Block>) prototype.getCodec();
            return codec.decode(raw);
        }
        return DataResult.error("方块数据格式错误");
    }

    private static Map<String, Object> asMap (RawObject raw) {
        if (raw.isMap()) return raw.asMap().get();
        return Map.of();
    }

    private static String getCellName (int x, int y) {
        return x + "," + y;
    }
}
