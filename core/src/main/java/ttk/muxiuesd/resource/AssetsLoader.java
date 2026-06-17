package ttk.muxiuesd.resource;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Util;

import java.util.HashMap;
import java.util.Objects;

/**
 * 游戏资源加载管理器
 * <p>
 * 每一个资源的文件路径都会对应一个id
 * */
public class AssetsLoader implements Disposable {
    public final String TAG = this.getClass().getName();

    private final AssetManager gameAssetManager;
    private final HashMap<String, AssetManager> modAssetManagers = new HashMap<>();  //每一个mod分配一个资源管理器
    /// 根据资源类型和资源的id来映射路径
    private final HashMap<Class<?>, HashMap<String, String>> typeToIdPathMap = new HashMap<>();

    private AssetsLoader () {
        //使用自定义的resolver
        this.gameAssetManager = new FightAssetManager();
    }

    private static final class Holder {
        private static final AssetsLoader INSTANCE = new AssetsLoader();
    }

    public static AssetsLoader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 加载资源（无回调）
     * @param id 路径映射id
     * @param filePath 资源文件的路径
     * @param type 资源类型
     * @param <T> 资源类型
     */
    public <T> void load (String id, String filePath, Class<T> type) {
        this.load(id, filePath, type, null);
    }

    /**
     * 加载资源
     * @param id 路径映射id
     * @param filePath 资源文件的路径
     * @param type 资源类型
     * @param callback 加载完成后的回调函数，可以为null
     * @param <T> 资源类型
     */
    public <T> void load (String id, String filePath, Class<T> type, Runnable callback) {
        //如果之前加载过这个id的相同路径的资源，就直接跳过，不重复加载
        if (Objects.equals(this.getPath(type, id), filePath)) {
            if (callback != null) callback.run();
            return;
        }

        AssetManager curManager = this.choiceAssetManager(id);

        if (!curManager.isLoaded(filePath, type)) {
            //没有加载就让他加载一遍
            this.singleLoad(curManager, filePath, type);
        }
        if (callback != null) callback.run();

        //最后不管怎么样都映射一遍
        this.addIdMapPath(type, id, filePath);
    }

    /**
     * 单次加载资源
     * */
    private <T> void singleLoad (AssetManager assetManager, String filePath, Class<T> type) {
        assetManager.load(filePath, type);
        assetManager.finishLoading();

        //检查一遍资源加载是否完成
        if (!assetManager.isLoaded(filePath, type)) {
            throw new IllegalStateException("资源加载失败: " + filePath);
        }
    }

    /**
     * 卸载资源
     * @param id 资源的id
     * @param type 资源类型
     * */
    public <T> void unload (String id, Class<T> type) {
        //检查是否有这个资源
        if (!this.containsId(type, id)) {
            Log.error(TAG, "资源类型：" + type + "中，没有加载过ID为：" + id + "的资源！！！");
            throw new IllegalStateException("无效ID：" + id);
        }

        AssetManager curManager = this.choiceAssetManager(id);
        String fileName = this.removeIdMapPath(type, id);
        curManager.unload(fileName);

        Log.print(TAG, "卸载类型：" + type + "中ID为：" + id + "的资源：" + fileName);
    }

    /**
     * 获取已加载的资源
     * @param id 资源路径映射的id
     * @param type 资源类型
     * @param <T> 资源类型
     * @return 已加载的资源
     */
    public <T> T getById (String id, Class<T> type) {
        if (!this.containsId(type, id)) {
            Log.error(TAG, "Id为：" + id + "的资源路径根本不存在！！！");
            throw new IllegalStateException("无效Id：" + id);
        }

        /*String[] split = id.split(":");
        if (Objects.equals(split[0], Fight.NAMESPACE)) {
            //先从游戏内部资源探查
            return this.getByPath(this.typeToIdPathMap.get(type).get(id), type);
        }else {
            //mod资源
            AssetManager modAssetManager = this.getModAssetManager(split[0]);
            return modAssetManager.get(this.typeToIdPathMap.get(type).get(id), type);
        }*/

        AssetManager curManager = this.choiceAssetManager(id);
        return curManager.get(this.getPath(type,  id), type);
    }

    /**
     * 获取已加载的资源
     * @param filePath 资源文件名
     * @param type 资源类型
     * @param <T> 资源类型
     * @return 已加载的资源
     */
    public  <T> T getByPath (String filePath, Class<T> type) {
        if (!this.gameAssetManager.isLoaded(filePath, type)) {
            throw new IllegalStateException("类型为："+ type.getName() + " 的资源未加载: " + filePath);
        }
        return this.gameAssetManager.get(filePath, type);
    }

    /**
     * 添加mod的资源管理，在mod最开始加载的时候添加
     * */
    public void addModAssetManager (String namespace) {
        if (!this.modAssetManagers.containsKey(namespace)) {
            this.modAssetManagers.put(namespace, new FightAssetManager());
        }else {
            throw new RuntimeException("命名空间为：" + namespace + " 的资源管理器不可重复添加！！！");
        }
    }

    /**
     * 获取mod自己的资源管理器
     * */
    public AssetManager getModAssetManager (String namespace) {
        return this.modAssetManagers.get(namespace);
    }

    /**
     * 根据id的命名空间来选择资源管理器
     * */
    public AssetManager choiceAssetManager (String id) {
        String[] split = Util.splitID(id);
        String namespace = split[0];
        if (Objects.equals(namespace, Fight.NAMESPACE)) {
            return this.gameAssetManager;
        }else {
            return this.getModAssetManager(namespace);
        }
    }

    /**
     * 根据判断指定的资源类型中是否有加载指定id的资源
     * */
    public <T> boolean containsId (Class<T> type, String id) {
        return this.typeToIdPathMap.get(type).containsKey(id);
    }

    /**
     * 添加id与资源路径的映射
     * <p>
     * 内部的为assets/里的路径
     * mod的为mod文件夹里的路径
     * */
    public <T> void addIdMapPath (Class<T> type, String id, String path) {
        //没有这个类型的map就添加一个
        if (!this.typeToIdPathMap.containsKey(type)) {
            this.typeToIdPathMap.put(type, new HashMap<>());
        }
        this.typeToIdPathMap.get(type).put(id, path);
    }

    /**
     * 移除id与资源路径的映射
     * */
    private <T> String removeIdMapPath (Class<T> type, String id) {
        return this.typeToIdPathMap.get(type).remove(id);
    }

    /**
     * 通过资源类型和资源的id获取文件路径
     * */
    public <T> String getPath (Class<T> type, String id) {
        if (!this.typeToIdPathMap.containsKey(type)) return "";

        return this.typeToIdPathMap.get(type).get(id);
    }

    @Override
    public void dispose() {
        this.gameAssetManager.dispose();
        this.modAssetManagers.forEach((manager, assetManager) -> {
            assetManager.dispose();
        });
        this.modAssetManagers.clear();
    }

}
