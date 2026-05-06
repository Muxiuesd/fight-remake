package game.muxiuesd.bedrockcore.app.interfaces.audio;

/**
 * 空间音频接听者
 * */
public interface SpatialAudioListener {

    void setPos (float x, float y, float z);

    void setOrientation (float forwardX, float forwardY, float forwardZ, float upX, float upY, float upZ);
}
