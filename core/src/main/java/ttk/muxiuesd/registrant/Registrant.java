package ttk.muxiuesd.registrant;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 实体，方块，墙，等等游戏元素的注册
 * 为之后的MOD做准备
 * */
public class Registrant<T> {
    private final String namespace; //命名空间
    private final HashMap<String, Supplier<? extends T>> regedit; //注册表，key为名称而不是id

    public Registrant(String namespace) {
        this.namespace = namespace;
        this.regedit = new HashMap<>();
    }

    /**
     * 注册
     * */
    public T register (String name, Supplier<? extends T> factory)  {
        if (this.contains(name)) {
            throw new RuntimeException("注册Id：" + this.getId(name) + " 重复！！！");
        }
        this.regedit.put(name, factory);
        return factory.get();
    }

    /**
     * 获取一个新的注册元素
     * */
    public T get(String name) {
        if (!this.contains(name)) {
            throw new RuntimeException("注册Id：" + this.getId(name) + " 不存在！！！");
        }
        return this.regedit.get(name).get();
    }


    public String getId (String name) {
        return namespace + ":" + name;
    }

    public boolean contains (String name) {
        return this.regedit.containsKey(name);
    }

    public HashMap<String, Supplier<? extends T>> getRegedit () {
        return this.regedit;
    }
}
