package ttk.muxiuesd.mod.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.mod.Mod;
import ttk.muxiuesd.mod.ModContainer;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * modApi：mod文件加载
 * <p>
 * 用于加载mod文件夹里的资源
 * TODO 增加能够加载的资源文件的种类
 * */
public class ModFileLoader {
    public static final String TAG = ModFileLoader.class.getName();

    public static final HashMap<String, ModFileLoader> loaders = new HashMap<String, ModFileLoader>();

    public final String modRoot;    //mod的根文件夹

    public ModFileLoader (String modRoot) {
        this.modRoot = modRoot;
    }

    /**
     * 获取指定命名空间的文件加载器。loader已存在时直接获取，不存在时新建一个
     * */
    public static ModFileLoader getFileLoader (String namespace) {
        ModContainer modContainer = ModContainer.getInstance();
        boolean hasMod = modContainer.hasMod(namespace);
        if (!hasMod) {
            Log.error(TAG, "不存在namespace为：" + namespace + " 的模组，无法加载此mod的文件加载器");
            throw new RuntimeException("不存在namespace为：" + namespace + " 的模组！！！");
        }

        if (loaders.containsKey(namespace)) {
            return loaders.get(namespace);
        }
        Mod mod = modContainer.get(namespace);
        ModFileLoader loader = new ModFileLoader(mod.getModPath());
        loaders.put(namespace, loader);
        return loader;
    }


    public <T> void load (String id, String path, Class<T> type) {
        this.load(id, path, type, null);
    }

    /**
     * 加载文件的核心方法
     * */
    public <T> void load (String id, String path, Class<T> type, ScriptObjectMirror callback) {
        String[] split = id.split(":");
        AssetManager modAssetManager = AssetsLoader.getInstance().getModAssetManager(split[0]);
        String filePath = this.getAbsolutePath(path);

        if (!modAssetManager.isLoaded(filePath)) {
            modAssetManager.load(filePath, type);
            modAssetManager.finishLoading();
            // 检查资源加载是否成功
            if (modAssetManager.isLoaded(filePath, type)) {
                AssetsLoader.getInstance().idMap(id, filePath);
                Log.print(TAG, "类型为：" + type.getName() + " 的资源：" + id + " 添加成功");
                if (callback != null) {
                    T file = modAssetManager.get(filePath, type);
                    callback.callMember("run", file);
                }// 资源加载完成，执行回调
            } else {
                throw new IllegalStateException("资源加载失败: " + filePath);
            }
        }else {
            if (callback != null) {
                T file = modAssetManager.get(filePath, type);
                callback.callMember("run", file);
            }
        }
    }


    private FileHandle getFileHandle (String path) {
        return Gdx.files.absolute(this.getAbsolutePath(path));
    }

    private String getAbsolutePath (String path) {
        return modRoot + path;
    }
}
