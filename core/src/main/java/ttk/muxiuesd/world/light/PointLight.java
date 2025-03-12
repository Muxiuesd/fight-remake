package ttk.muxiuesd.world.light;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * 点光源
 * 用于渲染
 * */
public class PointLight {
    private Vector2 position;   //光源的位置
    private Color color;        //光的颜色
    private float intensity;    //光源强度


    public PointLight (Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public PointLight (Vector2 position, Color color, float intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    public Vector2 getPosition () {
        return position;
    }

    public void setPosition (Vector2 position) {
        this.position = position;
    }

    public Color getColor () {
        return color;
    }

    public void setColor (Color color) {
        this.color = color;
    }

    public float getIntensity () {
        return intensity;
    }

    public void setIntensity (float intensity) {
        this.intensity = intensity;
    }
}
