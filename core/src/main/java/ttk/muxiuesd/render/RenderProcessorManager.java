package ttk.muxiuesd.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.IRenderTask;
import ttk.muxiuesd.render.abs.RenderProcessor;
import ttk.muxiuesd.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渲染处理器管理器
 * <p>
 * 游戏渲染的核心
 * */
public class RenderProcessorManager {
    private static final String TAG = RenderProcessorManager.class.getName();

    private static final ConcurrentHashMap<String, RenderProcessor> processors = new ConcurrentHashMap<>();
    private static final ArrayList<Map.Entry<String, Integer>> orderList = new ArrayList<>();

    /**
     * 获取指定名称的渲染处理器
     * */
    public static RenderProcessor get(String name) {
        if (!processors.containsKey(name)) {
            throw new RuntimeException("没有名为：" + name + " 的渲染处理器！！！");
        }
        return processors.get(name);
    }

    /**
     * 注册一个渲染处理器
     */
    public static String register(String name, RenderProcessor processor) {
        // 检查是否已存在并移除旧条目
        if (processors.containsKey(name)) {
            Log.error(TAG, "名为：" + name + " 的渲染处理器已存在，执行覆盖！！！");
            unregister(name);
        }

        processors.put(name, processor);

        // 同步更新排序列表
        synchronized (orderList) {
            orderList.add(Map.entry(name, processor.getRenderOrder()));
            orderList.sort(Comparator.comparingInt(Map.Entry::getValue));
        }

        return name;
    }

    public static void unregister(String name) {
        if (!processors.containsKey(name)) {
            throw new IllegalArgumentException("名为：" + name + " 的渲染处理器不存在，无法移除！！！");
        }

        processors.remove(name);

        //同步更新排序列表
        synchronized (orderList) {
            orderList.removeIf(entry -> entry.getKey().equals(name));
        }
    }

    /**
     * 执行所有的渲染处理器的渲染
     * */
    public static void render(Batch batch, ShapeRenderer shapeRenderer) {
        //复制排序列表以避免并发修改
        ArrayList<Map.Entry<String, Integer>> sortedList;
        synchronized (orderList) {
            sortedList = new ArrayList<>(orderList);
        }

        // 按照排序后的顺序执行渲染
        for (Map.Entry<String, Integer> entry : sortedList) {
            String key = entry.getKey();
            RenderProcessor processor = processors.get(key);
            if (processor != null) {
                processor.handleRender(batch, shapeRenderer);
            }
        }
    }

    /**
     * 根据接口类型添加渲染任务进相应的渲染处理器
     */
    public static void addRenderTask(IRenderTask task) {
        // 识别系统所在的渲染处理器
        for (RenderProcessor processor : processors.values()) {
            if (processor.recognize(task)) {
                break;
            }
        }
    }

    /**
     * 更新渲染处理器的顺序
     */
    public static void updateOrder(String name, int newOrder) {
        RenderProcessor processor = get(name);
        processor.setRenderOrder(newOrder);

        // 同步更新排序列表
        synchronized (orderList) {
            // 移除旧条目
            orderList.removeIf(entry -> entry.getKey().equals(name));
            // 添加新条目
            orderList.add(Map.entry(name, newOrder));
            // 重新排序
            orderList.sort(Comparator.comparingInt(Map.Entry::getValue));
        }
    }
}
