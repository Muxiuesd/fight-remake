package ttk.muxiuesd.world.particle.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.interfaces.Drawable;

/**
 * 粒子
 * */
public abstract class Particle implements Drawable, Pool.Poolable, Disposable {
    public TextureRegion region;    //材质需单独设置

    public Vector2 position;
    public Vector2 velocity;
    public Vector2 origin;
    public Vector2 startSize;
    public Vector2 endSize;
    public Vector2 curSize;
    public Vector2 scale;

    public float rotation;
    public float duration;
    public float lifetime;

    public Particle() {

    }

    /**
     * 初始化粒子
     * */
    public void init () {
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.origin = new Vector2();
        this.startSize = new Vector2();
        this.endSize = new Vector2();
        this.curSize = new Vector2();
        this.scale = new Vector2(1f, 1f);   //默认缩放
    }

    @Override
    public void draw (Batch batch) {
        if (this.region != null) {
            batch.draw(this.region,
                this.position.x, this.position.y,
                this.origin.x, this.origin.y,
                this.curSize.x, curSize.y,
                this.scale.x, this.scale.y,
                this.rotation);
        }
    }

    @Override
    public void reset () {
        //this.region = null;
        this.position.setZero();
        this.velocity.setZero();
        this.origin.setZero();
        this.startSize.setZero();
        this.endSize.setZero();
        this.curSize.setZero();
        this.scale.setZero();
        this.rotation = 0;
        this.lifetime = 0;
        this.duration = 0;
    }

    @Override
    public void dispose () {
        this.region = null;
        this.position = null;
        this.velocity = null;
        this.origin = null;
        this.startSize = null;
        this.endSize = null;
        this.curSize = null;
        this.scale = null;
    }
}
