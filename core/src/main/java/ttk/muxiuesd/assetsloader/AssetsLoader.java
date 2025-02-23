package ttk.muxiuesd.assetsloader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;
import java.util.Objects;

/**
 * 游戏资源加载管理器
 * 通过唯一的id获取对应的资源
 * */
public class AssetsLoader implements Disposable {
    public final String TAG = this.getClass().getSimpleName();

    private final AssetManager gameAssetManager = new AssetManager();
    private final HashMap<String, AssetManager> modAssetManagers = new HashMap<>();  //每一个mod分配一个资源管理器
    private final AsyncExecutor asyncExecutor = new AsyncExecutor(10);

    private final HashMap<String, String> idToPath; //id映射路径，规范id例子： fight:grass_block

    private AssetsLoader () {
        this.idToPath = new HashMap<>();
    }

    private static final class Holder {
        private static final AssetsLoader INSTANCE = new AssetsLoader();
    }

    public static AssetsLoader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 异步加载资源
     * @param id 路径映射id
     * @param filePath 资源文件的路径
     * @param type 资源类型
     * @param callback 加载完成后的回调函数，可以为null
     * @param <T> 资源类型
     */
    public <T> void loadAsync(String id, String filePath, Class<T> type, Runnable callback) {
        if (this.containsId(id)) {
            //已存在则直接执行回调中的加载
            Gdx.app.postRunnable(callback);
            return;
        }
        this.loadAsync(filePath, type, callback);
        this.idToPath.put(id, filePath);
    }

    public <T> void loadAsync(String filePath, Class<T> type, Runnable callback) {
        if (!this.gameAssetManager.isLoaded(filePath, type)) {
            this.gameAssetManager.load(filePath, type);

            // 使用Gdx.app.postRunnable，确保在主线程中处理
            Gdx.app.postRunnable(() -> {
                try {
                    // 在主线程中等待加载完成
                    this.gameAssetManager.finishLoading();
                    // 检查资源加载是否成功
                    if (this.gameAssetManager.isLoaded(filePath, type)) {
                        if (callback != null) {
                            callback.run();
                        }// 资源加载完成，执行回调
                    } else {
                        throw new IllegalStateException("资源加载失败: " + filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // 捕获异常并打印
                }
            });
        } else {
            // 如果已经加载，直接在主线程中执行回调
            if (callback != null) {
                Gdx.app.postRunnable(callback);
            }
        }
    }

    /**
     * 获取已加载的资源
     * @param id 资源路径映射的id
     * @param type 资源类型
     * @param <T> 资源类型
     * @return 已加载的资源
     */
    public <T> T getById(String id, Class<T> type) {
        if (!this.containsId(id)) {
            Log.error(TAG, "Id为：" + id + "的资源路径根本不存在！！！");
            throw new IllegalStateException("无效Id" + id);
        }
        String[] split = id.split(":");
        if (Objects.equals(split[0], Fight.NAMESPACE)) {
            //先从游戏内部资源探查
            return this.gameAssetManager.get(this.idToPath.get(id), type);
        }else {
            //mod资源
            AssetManager modAssetManager = this.getModAssetManager(split[0]);
            return modAssetManager.get(this.idToPath.get(id), type);
        }
        //throw new RuntimeException();
    }

    /**
     * 获取已加载的资源
     * @param filePath 资源文件名
     * @param type 资源类型
     * @param <T> 资源类型
     * @return 已加载的资源
     * TODO 逐渐弃用这个
     */
    public <T> T get(String filePath, Class<T> type) {
        if (!this.gameAssetManager.isLoaded(filePath, type)) {
            throw new IllegalStateException("资源未加载: " + filePath);
        }
        return this.gameAssetManager.get(filePath, type);
    }

    /**
     * 添加mod的资源管理，在mod最开始加载的时候添加
     * */
    public void addModAssetManager(String namespace) {
        if (!this.modAssetManagers.containsKey(namespace)) {
            this.modAssetManagers.put(namespace, new AssetManager());
        }else {
            throw new RuntimeException("命名空间为：" + namespace + " 的资源管理器不可重复添加！！！");
        }
    }

    /**
     * 获取mod自己的资源管理器
     * */
    public AssetManager getModAssetManager(String namespace) {
        return this.modAssetManagers.get(namespace);
    }

    public boolean containsId(String id) {
        return this.idToPath.containsKey(id);
    }

    /**
     * 内部的为assets里的路径
     * mod的为mod文件加里的路径
     * */
    public void idMap (String id, String path) {
        this.idToPath.put(id, path);
    }

    @Override
    public void dispose() {
        this.gameAssetManager.dispose();
        this.asyncExecutor.dispose();
    }

}
