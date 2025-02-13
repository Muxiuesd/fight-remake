package ttk.muxiuesd.assetsloader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.async.AsyncExecutor;

public class AssetsLoader implements Disposable {
    private final AssetManager assetManager = new AssetManager();
    private final AsyncExecutor asyncExecutor = new AsyncExecutor(10);

    private static final class Holder {
        private static final AssetsLoader instance = new AssetsLoader();
    }

    public static AssetsLoader getInstance() {
        return Holder.instance;
    }

    /**
     * 异步加载资源
     * @param filePath 资源文件的路径
     * @param type 资源类型
     * @param callback 加载完成后的回调函数
     * @param <T> 资源类型
     */
    public <T> void loadAsync(String filePath, Class<T> type, Runnable callback) {
        if (!assetManager.isLoaded(filePath, type)) {
            assetManager.load(filePath, type);

            // 使用Gdx.app.postRunnable，确保在主线程中处理
            Gdx.app.postRunnable(() -> {
                try {
                    // 在主线程中等待加载完成
                    assetManager.finishLoading();
                    // 检查资源加载是否成功
                    if (assetManager.isLoaded(filePath, type)) {
                        callback.run(); // 资源加载完成，执行回调
                    } else {
                        throw new IllegalStateException("资源加载失败: " + filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // 捕获异常并打印
                }
            });
        } else {
            // 如果已经加载，直接在主线程中执行回调
            Gdx.app.postRunnable(callback);
        }
    }

    /**
     * 获取已加载的资源
     * @param filePath 资源文件名
     * @param type 资源类型
     * @param <T> 资源类型
     * @return 已加载的资源
     */
    public <T> T get(String filePath, Class<T> type) {
        if (!assetManager.isLoaded(filePath, type)) {
            throw new IllegalStateException("资源未加载: " + filePath);
        }
        return assetManager.get(filePath, type);
    }

    public AssetManager getAssetManager() {
        return this.assetManager;
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        asyncExecutor.dispose();
    }

}
