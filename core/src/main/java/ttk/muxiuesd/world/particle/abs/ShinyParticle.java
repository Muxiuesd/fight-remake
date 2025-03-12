package ttk.muxiuesd.world.particle.abs;

import com.badlogic.gdx.graphics.Color;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.light.PointLight;

/**
 * 发光粒子
 * */
public abstract class ShinyParticle extends Particle implements Updateable {
    public PointLight light;

    public ShinyParticle (Color color, float intensity) {
        this.light = new PointLight(color, intensity);
    }

    @Override
    public void update (float delta) {
        //更新光源位置
        this.light.setPosition(position);
    }

    @Override
    public void reset () {
        super.reset();
        this.light.setPosition(position);
    }
}
