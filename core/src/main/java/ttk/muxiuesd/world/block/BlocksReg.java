package ttk.muxiuesd.world.block;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.instance.*;

import java.util.HashMap;

/**
 * 方块注册
 * */
public class BlocksReg {
    public static final String TAG = BlocksReg.class.getName();

    static Registrant<Block> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Block.class);
    static {

    }

    public static void registerAllBlocks () {
        register("block_test", new BlockTest());
        register("grass", new BlockGrass());
        register("stone", new BlockStone());
        register("sand", new BlockSand());
        register("water", new BlockWater());
        Log.print(TAG, "游戏方块注册完毕");
    }

    private static void register (String id, Block block) {
        registrant.register(id, block);
    }

    public static Block newBlock (String id) {
        return registrant.get(id);
    }

    /**
     * 打印注册的方块
     * TODO 打印mod里注册的方块
     * */
    public static void printAllBlock () {
        HashMap<String, Block> map = registrant.getR();
        Array<String> allBlockName = new Array<>();
        map.keySet().forEach(allBlockName::add);
        Log.print(BlocksReg.class.getName(), "注册的方块有：");
        for (int i = 0; i < allBlockName.size; i++) {
            if (i + 1 < allBlockName.size) {
                System.out.print(allBlockName.get(i) + " | ");
            }else {
                System.out.println(allBlockName.get(i));
            }
        }
    }
}
