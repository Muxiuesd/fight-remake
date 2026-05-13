package game.muxiuesd.bedrockcore.app.interfaces.audio;

import com.badlogic.gdx.graphics.Camera;

/**
 * 空间音频接听者
 * */
public interface SpatialAudioListener {

    void setPos (float x, float y, float z);

    void setOrientation (Camera camera);
}
