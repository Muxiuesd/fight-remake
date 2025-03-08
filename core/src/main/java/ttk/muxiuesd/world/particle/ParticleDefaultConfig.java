package ttk.muxiuesd.world.particle;

import com.badlogic.gdx.math.Vector2;

/**
 * 粒子默认参数
 * TODO 添加许多默认参数，方便使用，id化（也许）
 * */
public class ParticleDefaultConfig {
    //注册不同的默认粒子参数
    public static final ParticleDefaultConfig ShootParticle = registry(
        null, null, new Vector2(0.25f, 0.25f),
        new Vector2(0.5f, 0.5f), new Vector2(0.1f, 0.1f), new Vector2(1f, 1f),
        0f, 1f);


    public Vector2 position;
    public Vector2 velocity;
    public Vector2 origin;
    public Vector2 startSize;
    public Vector2 endSize;
    public Vector2 scale;

    public float rotation;
    public float duration;

    public ParticleDefaultConfig (Vector2 position, Vector2 velocity,
                                  Vector2 origin, Vector2 startSize, Vector2 endSize,
                                  Vector2 scale, float rotation, float duration) {
        this.position = position;
        this.velocity = velocity;
        this.origin = origin;
        this.startSize = startSize;
        this.endSize = endSize;
        this.scale = scale;
        this.rotation = rotation;
        this.duration = duration;
    }

    public static ParticleDefaultConfig registry (Vector2 position, Vector2 velocity,
                                                  Vector2 origin, Vector2 startSize, Vector2 endSize,
                                                  Vector2 scale, float rotation, float duration) {
        return new ParticleDefaultConfig(
            position, velocity,
            origin, startSize, endSize,
            scale, rotation, duration);
    }
}
