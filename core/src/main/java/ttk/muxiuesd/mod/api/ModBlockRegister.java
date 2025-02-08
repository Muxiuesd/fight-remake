package ttk.muxiuesd.mod.api;

import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.Block;

/**
 * mod专用：Block注册器
 * */
public class ModBlockRegister {
    private final String namespace;
    private final Registrant<Block> registrant;

    public ModBlockRegister (String namespace) {
        this.namespace = namespace;
        this.registrant = this.getRegistrant();
    }

    public Registrant<Block> getRegistrant() {
        return RegistrantGroup.getRegistrant(this.namespace, Block.class);
    }

    public Registrant<Block> registry (String id, Block block) {
        if (id == null || id.isEmpty() || block == null) {
            Log.error(this.getTag(), "id、block都不能为null！！！");
            return null;
        }
        Registrant<Block> result = this.registrant.register(id, block);
        if (result == null) {
            Log.error(this.getTag(), "id：" + id + " 重复！！！");
            return null;
        }
        Log.print(this.getTag(), "方块：" + this.namespace +":"+id + " 注册成功");
        return result;
    }

    private String getTag () {
        return "Mod:" + this.namespace + ":BlockRegister";
    }
}
