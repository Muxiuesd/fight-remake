package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.ID;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * 实体，方块，墙，等等游戏元素的注册
 * 为之后的MOD做准备
 * */
public class Registrant<T extends ID> {
    private final String namespace; //命名空间
    private final HashMap<String, Supplier<? extends T>> regedit; //注册表，key为名称而不是id
    private final HashMap<String, Supplier<? extends T>> elementSuppliers;   //key为被注册的元素的简单类名

    public Registrant(String namespace) {
        this.namespace = namespace;
        this.regedit = new HashMap<>();
        this.elementSuppliers = new HashMap<>();
    }

    /**
     * 注册
     * */
    public T register (String name, Supplier<? extends T> factory)  {
        if (this.contains(name)) {
            throw new RuntimeException("注册Id：" + this.getId(name) + " 重复！！！");
        }
        this.regedit.put(name, factory);
        T element = factory.get();
        String key = element.getClass().getName();
        this.elementSuppliers.put(key, factory);
        return element;
    }

    /**
     * 获取一个新的注册元素
     * */
    public T get(String name) {
        if (!this.contains(name)) {
            throw new RuntimeException("注册Id：" + this.getId(name) + " 不存在！！！");
        }
        T t = this.regedit.get(name).get();
        t.setID(this.getId(name));
        return t;
    }

    public Supplier<? extends T> getSupplier(T element) {
        String simpleName = element.getClass().getName();
        if (!this.elementSuppliers.containsKey(simpleName)) {
            throw new RuntimeException("游戏元素：" + element + " 没有注册过，无法获取其提供者！！！");
        }
        return this.elementSuppliers.get(simpleName);
    }

    public String getId (String name) {
        return this.namespace + ":" + name;
    }

    public boolean contains (String name) {
        return this.regedit.containsKey(name);
    }

    public HashMap<String, Supplier<? extends T>> getRegedit () {
        return this.regedit;
    }

    public HashMap<String, Supplier<? extends T>> getElementSuppliers() {
        return this.elementSuppliers;
    }
}
