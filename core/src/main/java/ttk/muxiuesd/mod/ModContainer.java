package ttk.muxiuesd.mod;

import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * 所有mod的储存
 * 应该是唯一的
 * */
public class ModContainer {
    public final String TAG = this.getClass().getName();

    private static ModContainer singleton;
    private final HashMap<String, Mod> container = new HashMap<>();

    private ModContainer() {
    }

    /**
     * 单例模式
     * */
    public static ModContainer getInstance() {
        if (singleton == null) {
            singleton = new ModContainer();
        }
        return singleton;
    }

    public void add(String namespace, Mod mod) {
        if (this.hasMod(namespace)) {
            Log.error(TAG, "Mod的命名空间：" + namespace + "已经存在，不可重复添加！！！");
            return;
        }
        this.container.put(namespace, mod);
    }

    public Mod remove(String namespace) {
        if (this.hasMod(namespace)) {
            return this.container.remove(namespace);
        }
        Log.error(TAG, "Mod的命名空间：" + namespace + "不存在，无法删除！！！");
        return null;
    }

    public Mod get(String namespace) {
        if (this.hasMod(namespace)) {
            return container.get(namespace);
        }
        return null;
    }

    public boolean hasMod(String namespace) {
        return this.container.containsKey(namespace);
    }

    /**
     * 获取所有的mod
     * */
    protected HashMap<String, Mod> getAllMods () {
        return this.container;
    }

    public boolean isEmpty () {
        return this.container.isEmpty();
    }
}
