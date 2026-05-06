package game.muxiuesd.bedrockcore.app.interfaces.audio;

import com.badlogic.gdx.math.Vector3;

/**
 * 发出空间音效的声音源
 * */
public interface SpatialAudioSource {
    /**
     * 返回声源在世界坐标系中的当前位置
     * */
    Vector3 getPos();

    /**
     * 返回声源的前方朝向（默认朝向 -Z）
     * */
    default Vector3 getForward () {
        return new Vector3(0, 0, -1);
    }

    /**
     * 返回声源的上方向（默认 Y 轴向上）
     * */
    default Vector3 getUp () {
        return new Vector3(0, 1, 0);
    }
}
