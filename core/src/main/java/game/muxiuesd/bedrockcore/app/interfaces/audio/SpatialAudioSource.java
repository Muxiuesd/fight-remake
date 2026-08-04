package game.muxiuesd.bedrockcore.app.interfaces.audio;

import com.badlogic.gdx.math.Vector3;

/**
 * 发出空间音效的声音源
 * */
public interface SpatialAudioSource {
    Vector3 DEFAULT_FORWARD = new Vector3(0, 0, -1f);
    Vector3 DEFAULT_UP = new Vector3(0, 1f, 0);

    /**
     * 返回声源在世界坐标系中的当前位置
     * */
    Vector3 getPos();

    /**
     * 返回声源的前方朝向（默认朝向 -Z）
     * */
    default Vector3 getForward () {
        return DEFAULT_FORWARD;
    }

    /**
     * 返回声源的上方向（默认 Y 轴向上）
     * */
    default Vector3 getUp () {
        return DEFAULT_UP;
    }
}
