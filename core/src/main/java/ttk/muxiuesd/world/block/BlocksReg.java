package ttk.muxiuesd.world.block;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.*;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 方块注册
 * */
public class BlocksReg {
    public static final String TAG = BlocksReg.class.getName();
    public static final Registrant<Block> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Block.class);

    public static void registerAllBlocks () {
        Log.print(TAG, "游戏方块注册完毕");
    }

    public static final Block TEST_BLOCK = register("block_test", BlockTest::new);
    public static final Block GRASS = register("grass", BlockGrass::new);
    public static final Block STONE = register("stone", BlockStone::new);
    public static final Block SAND = register("sand", BlockSand::new);
    public static final Block WATER = register("water", BlockWater::new);

    public static Block register (String name, Supplier<Block> supplier) {
        return registrant.register(name, supplier);
    }

    public static Block newBlock (String name) {
        return registrant.get(name);
    }

    /**
     * 打印注册的方块
     * TODO 打印mod里注册的方块
     * */
    public static void printAllBlock () {
        HashMap<String, Supplier<? extends Block>> map = registrant.getRegedit();
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
