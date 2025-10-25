package ttk.muxiuesd.util;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * 向下凹的曲线绘制
 * <p>
 * 例子：鱼线绘制
 * */
public class CurveDrawer {
    private static final int SEGMENTS = 10; // 曲线平滑度

    /**
     * 绘制可调曲率的二次贝塞尔曲线
     * @param renderer 图形渲染器
     * @param start 起点
     * @param end 终点
     * @param curvature 曲率系数（负值向下凹）
     */
    public static void drawCurve(ShapeRenderer renderer,
                                       Vector2 start, Vector2 end,
                                       float curvature) {
        Vector2 controlPoint = calculateControlPoint(start, end, curvature);
        Vector2[] points = calculateBezierPoints(start, end, controlPoint);

        for (int i = 0; i < points.length - 1; i++) {
            Vector2 p1 = points[i];
            Vector2 p2 = points[i + 1];
            renderer.line(p1.x, p1.y, p2.x, p2.y);
        }
    }

    // 计算控制点
    private static Vector2 calculateControlPoint(Vector2 start, Vector2 end, float curvature) {
        Vector2 mid = new Vector2().add(start).add(end).scl(0.5f);
        Vector2 dir = new Vector2(end).sub(start);

        // 计算垂直方向（顺时针90度旋转）
        Vector2 perpendicular = new Vector2(-dir.y, dir.x).nor();
        return mid.add(perpendicular.scl(curvature));
    }

    // 生成贝塞尔曲线点
    private static Vector2[] calculateBezierPoints(Vector2 start, Vector2 end, Vector2 control) {
        Vector2[] points = new Vector2[SEGMENTS + 1];
        for (int i = 0; i <= SEGMENTS; i++) {
            float t = i / (float) SEGMENTS;
            points[i] = quadraticBezier(start, control, end, t);
        }
        return points;
    }

    // 二次贝塞尔公式
    private static Vector2 quadraticBezier(Vector2 p0, Vector2 p1, Vector2 p2, float t) {
        float u = 1 - t;
        float x = u*u*p0.x + 2*u*t*p1.x + t*t*p2.x;
        float y = u*u*p0.y + 2*u*t*p1.y + t*t*p2.y;
        return new Vector2(x, y);
    }
}
