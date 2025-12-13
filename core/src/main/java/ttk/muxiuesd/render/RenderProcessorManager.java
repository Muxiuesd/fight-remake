package ttk.muxiuesd.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IRenderTask;
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

    private static ArrayList<Map.Entry<String, Integer>> sortedList = new ArrayList<>();

    /**
     * 获取指定名称的渲染处理器
     * */
    public static RenderProcessor get (String name) {
        if (!processors.containsKey(name)) {
            throw new RuntimeException("没有名为：" + name + " 的渲染处理器！！！");
        }
        return processors.get(name);
    }

    /**
     * 注册一个渲染处理器
     */
    public static String register (String name, RenderProcessor processor) {
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
     * 排序
     * */
    public static void sort() {
        //TODO 排序
        sortedList.clear();
        sortedList.addAll(orderList);
    }

    public static void batchRender(Batch batch) {
        // 按照排序后的顺序执行渲染
        for (Map.Entry<String, Integer> entry : sortedList) {
            String key = entry.getKey();
            RenderProcessor processor = processors.get(key);
            if (processor != null) {
                processor.beginShader(batch);
                processor.handleBatchRender(batch);
                processor.endShader();
            }
        }
    }

    public static void shapeRender(ShapeRenderer shapeRenderer) {
        // 按照排序后的顺序执行渲染
        for (Map.Entry<String, Integer> entry : sortedList) {
            String key = entry.getKey();
            RenderProcessor processor = processors.get(key);
            if (processor != null) {
                processor.handleShapeRender(shapeRenderer);
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
     * 根据渲染任务的接口类型从相应的渲染处理器中移除
     * */
    public static void removeRenderTask(IRenderTask task) {
        boolean removed = false;
        for (RenderProcessor processor : processors.values()) {
            if (processor.getRenderTasks().contains(task)) {
                if (processor.getRenderTasks().remove(task)) {
                    removed = true;
                    break;
                }
            }
        }
        if (removed) {
            Log.print(TAG, "渲染任务：" + task + " 已被移除！");
        }else {
            Log.error(TAG, "渲染任务：" + task + " 没有被移除成功！！！或许并未被添加过！！！");
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
