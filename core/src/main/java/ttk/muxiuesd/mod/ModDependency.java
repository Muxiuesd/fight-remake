package ttk.muxiuesd.mod;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.SemanticVersion;

import java.util.HashMap;

/**
 * mod依赖关系分析类
 * */
public class ModDependency {
    public static String TAG = ModDependency.class.getName();

    /**
     * 进行分析
     * */
    protected static HashMap<String, ModNode> analysis () {
        HashMap<String, Mod> mods = ModContainer.getInstance().getAllMods();
        //给所有mod创建节点
        HashMap<String, ModNode> nodes = new HashMap<>(mods.size());

        for (Mod mod : mods.values()) {
            nodes.put(mod.getModNamespace(), new ModNode(mod));
        }

        for (Mod mod : mods.values()) {
            JsonValue info = mod.getInfo();
            JsonValue dependence = info.get("dependence");
            //JsonValue dependence1 = info.getChild("dependence");
            //没有依赖则跳过对这个mod的依赖分析
            if (dependence == null || !dependence.isArray() || !dependence.notEmpty()) {
                continue;
            }
            //获取被分析mod的节点
            ModNode node = nodes.get(mod.getModNamespace());

            //对每个依赖进行解析
            for (int i = 0; i < dependence.size; i++) {
                JsonValue dependency = dependence.get(i);
                String namespace = dependency.get("namespace").asString();
                if (!mods.containsKey(namespace)) {
                    //报错
                    Log.error(TAG, "Mod：" + mod.getModName()
                        + " 需要依赖命名空间为：" + namespace + " 的Mod，但此Mod并未加载！！！");
                    throw new RuntimeException();
                }
                //获取库mod
                Mod libMod = mods.get(namespace);
                //获取版本号并进行比较
                String version1 = dependency.getString("version");
                String version2 = libMod.getInfo().getString("version");
                SemanticVersion neededVersion = new SemanticVersion(version1);
                SemanticVersion libModVersion = new SemanticVersion(version2);

                //库的版本小于被分析的mod所依赖的版本
                if (libModVersion.compareTo(neededVersion) < 0) {
                    //报错
                    Log.error(TAG, "Mod：" + mod.getModName()
                        + " 需要依赖命名空间为：" + namespace + " 的库Mod的版本需要大于：" + version1 +
                        "，但目前所加载的库Mod版本为：" + version2 + " ！！！");
                    throw new RuntimeException();
                }

                //到这里就通过一次解析了，把作为依赖库的mod节点加入被分析的mod的子节点集合里
                node.addChild(nodes.get(libMod.getModNamespace()));
            }
        }

        //到这里完成所有mod的依赖关系的分析
        nodes.forEach((namespace, node) -> {
            node.getChildren().forEach(child -> {
                Log.print(TAG, "Mod：" + namespace + " 依赖的Mod有：" + child.mod.getModNamespace());
            });
        });

        return nodes;
    }
}
