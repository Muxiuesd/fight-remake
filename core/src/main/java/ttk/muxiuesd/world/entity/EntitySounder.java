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

    public EntitySounder (Entity<?> entity) {
        this.entity = entity;
    }

    @Override
    public Vector3 getPos () {
        Vector2 centerPos = this.entity.getCenterPos();
        return new Vector3(centerPos, 0f);
    }

    @Override
    public Vector3 getForward () {
        //如果实体没有速度，那就使用默认朝向
        if (this.entity.getCurSpeed() == 0f) {
            return SpatialAudioSource.super.getForward();
        }
        //如果实体有速度，就以速度方向为实体的前方朝向
        return new Vector3(this.entity.getVelocity(), 0f);
    }

    @Override
    public Vector3 getUp () {
        return SpatialAudioSource.super.getUp();
    }
}
