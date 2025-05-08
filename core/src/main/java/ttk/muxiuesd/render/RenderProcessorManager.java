package ttk.muxiuesd.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.render.abs.RenderProcessor;
import ttk.muxiuesd.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 渲染处理器管理器
 * */
public class RenderProcessorManager {
    private static String TAG = RenderProcessorManager.class.getName();

    private static ConcurrentHashMap<String, RenderProcessor> processors = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> renderOrders = new ConcurrentHashMap<>();
    private static List<Map.Entry<String, Integer>> orderList = new ArrayList<>();

    public static RenderProcessor get(String name) {
        if (!processors.containsKey(name)) {
            throw new RuntimeException("没有名为：" + name + " 的渲染处理器！！！");
        }
        return processors.get(name);
    }

    public static String register(String name, RenderProcessor processor) {
        if (processors.containsKey(name)) {
            Log.error(TAG, "名为：" + name + " 的渲染处理器已存在，则执行覆盖！！！");
        }
        processors.put(name, processor);
        renderOrders.put(name, processor.getRenderOrder());

        //渲染顺序排序
        // 将键值对转换为列表
        orderList.add(Map.entry(name, processor.getRenderOrder()));
        // 对列表进行排序
        orderList.sort(Comparator.comparingInt(Map.Entry::getValue));

        return name;
    }

    public static void render (Batch batch, ShapeRenderer shapeRenderer) {
        // 按照排序后的顺序执行渲染
        for (Map.Entry<String, Integer> entry : orderList) {
            String key = entry.getKey();
            RenderProcessor processor = processors.get(key);
            if (processor != null) {
                processor.handleRender(batch, shapeRenderer);
            }
        }
    }
}
