package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.ChunkTraversalJob;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.wall.Wall;

import java.util.Optional;

/**
 * 对于区块的编解码
 * */
public class ChunkCodec extends JsonCodec<Chunk> {
    @Override
    public void encode (Chunk chunk, JsonDataWriter dataWriter) {
        //方块
        this.encodeTraversal("blocks", dataWriter, chunk, (x, y) -> {
            Block block = chunk.getBlock(x, y);
            dataWriter.objStart(this.getObjName(x, y));
            Codecs.BLOCK.encode(block, dataWriter);
            dataWriter.objEnd();
        });


        //墙体
        this.encodeTraversal("walls", dataWriter, chunk, (x, y) -> {
            Wall<?> wall = chunk.getWall(x, y);
            if (wall != null) {
                dataWriter.objStart(this.getObjName(x, y));
                Codecs.WALL.encode(wall, dataWriter);
                dataWriter.objEnd();
            }
        });


        //高度信息
        this.encodeTraversal("heights", dataWriter, chunk, (x, y) -> {
            int height = chunk.getHeight(x, y);
            dataWriter.writeInt(this.getObjName(x, y), height);
        });

    }

    @Override
    protected Optional<Chunk> parse (JsonDataReader dataReader) {
        Chunk resultChunk = new Chunk();
        //解析方块
        this.decodeTraversal("blocks", dataReader, resultChunk,
            (blocksObj, chunk, x, y) -> {
                //默认方块，查找不到就这个方块
                Block block = Blocks.ARI;

                String name = this.getObjName(x, y);
                JsonValue blockValue = blocksObj.get(name);
                //对每一个方块json进行解析
                Optional<Block> optional = Codecs.BLOCK.decode(new JsonDataReader(blockValue));
                if (optional.isPresent()) {
                    block = optional.get();
                }
                resultChunk.setBlock(block, x, y);
        });

        //解析墙体
        this.decodeTraversal("walls", dataReader, resultChunk,
            (wallsObj, chunk, x, y) -> {
                JsonValue wallValue = wallsObj.get(this.getObjName(x, y));
                if (wallValue != null) {
                    Optional<Wall<?>> optional = Codecs.WALL.decode(new JsonDataReader(wallValue));
                    optional.ifPresent(wall -> resultChunk.setWall(wall, x, y));
                }
        });

        //解析高度
        this.decodeTraversal("heights", dataReader, resultChunk,
            (heightsObj, chunk, x, y) -> {
                int height= heightsObj.getInt(this.getObjName(x, y), 1);
                chunk.setHeight(x, y, height);
        });

        return Optional.of(resultChunk);
    }

    private void encodeTraversal (String objName, JsonDataWriter dataWriter, Chunk chunk, ChunkTraversalJob job) {
        dataWriter.objStart(objName);
        chunk.traversal(job);
        dataWriter.objEnd();
    }

    private void decodeTraversal (String objName, JsonDataReader dataReader, Chunk chunk, DecodeTraversalJob job) {
        JsonValue obj = dataReader.readObj(objName);
        chunk.traversal((x, y) -> {
            job.execute(obj, chunk, x, y);
        });
    }

    private String getObjName (int x, int y) {
        return x + "," + y;
    }

    private interface DecodeTraversalJob {
        void execute(JsonValue obj, Chunk chunk, int x, int y);
    }
}
