package ttk.muxiuesd.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.abs.RenderProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渲染处理器管理器
 * <p>
 * 游戏渲染的核心，需要执行渲染的处理器就注册，不需要的就取消注册
 * */
public class RenderProcessorManager {
    private static final String TAG = RenderProcessorManager.class.getName();

    private static final ConcurrentHashMap<String, RenderProcessor> processors = new ConcurrentHashMap<>();
    /// 根据渲染优先级大小来排序后的渲染处理器列表
    private static final ArrayList<Map.Entry<String, Integer>> orderList = new ArrayList<>();
    /// 根据渲染顺序来排序后的渲染处理器列表
    private static final ArrayList<Map.Entry<String, Integer>> sortedList = new ArrayList<>();


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
        //检查是否已存在并移除旧条目
        if (processors.containsKey(name)) {
            Log.error(TAG, "名为：" + name + " 的渲染处理器已存在，执行覆盖！！！");
            unregister(name);
        }
        processors.put(name, processor);

        //根据渲染处理器的优先级大小排序
        synchronized (orderList) {
            orderList.add(Map.entry(name, processor.getRenderOrder()));
            orderList.sort(Comparator.comparingInt(Map.Entry::getValue));
        }
        return name;
    }

    /**
     * 取消注册
     * */
    public static void unregister (String name) {
        if (!processors.containsKey(name)) {
            throw new IllegalArgumentException("名为：" + name + " 的渲染处理器不存在，无法移除！！！");
        }
        processors.remove(name);

        //从顺序列表中移除
        synchronized (orderList) {
            orderList.removeIf(entry -> entry.getKey().equals(name));
        }
    }

    /**
     * 交换，每一帧渲染前调用
     * */
    public static void swap () {
        sortedList.clear();
        sortedList.addAll(orderList);
    }

    /**
     * 单独处理图形渲染
     * */
    public static void batchRender (Batch batch) {
        //按照排序后的顺序执行渲染
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

    /**
     * 单独处理形状渲染
     * */
    public static void shapeRender (ShapeRenderer shapeRenderer) {
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
     * 交叉渲染
     * */
    public static void handleRender (Batch batch, ShapeRenderer shapeRenderer) {
        // 按照排序后的顺序执行渲染
        for (Map.Entry<String, Integer> entry : sortedList) {
            String key = entry.getKey();
            RenderProcessor processor = processors.get(key);
            if (processor != null) {
                batch.begin();
                shapeRenderer.begin();

                processor.beginShader(batch);
                processor.handleBatchRender(batch);
                processor.handleShapeRender(shapeRenderer);
                processor.endShader();

                batch.end();
                shapeRenderer.end();
            }
        }
    }

    /**
     * 根据接口类型添加渲染任务进相应的渲染处理器
     */
    public static void addRenderTask (IRenderTask task) {
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
    public static void removeRenderTask (IRenderTask task) {
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
            Log.error(TAG, "渲染任务：" + task + " 没有被移除成功，或许并未被添加过！！！");
        }
    }
}
