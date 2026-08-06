package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体音效发声者
 * */
public class EntitySounder implements SpatialAudioSource {
    private final Entity<?> entity;
    private final Vector3 posCache = new Vector3();
    private final Vector3 forwardCache = new Vector3();

    public EntitySounder (Entity<?> entity) {
        this.entity = entity;
    }

    @Override
    public Vector3 getPos () {
        Vector2 centerPos = this.entity.getCenterPos();
        return this.posCache.set(centerPos.x, centerPos.y, 0f);
    }

    @Override
    public Vector3 getForward () {
        if (this.entity.getCurSpeed() == 0f) {
            return SpatialAudioSource.DEFAULT_FORWARD;
        }
        Vector2 velocity = this.entity.getVelocity();
        return this.forwardCache.set(velocity.x, velocity.y, 0f);
    }

    @Override
    public Vector3 getUp () {
        return SpatialAudioSource.DEFAULT_UP;
    }
}
