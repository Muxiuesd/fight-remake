package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.graphics.Camera;
import de.pottgames.tuningfork.SoundListener;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioListener;

/**
 * TuningFork实现的音频接听者
 * */
public class TFAudioListener implements SpatialAudioListener {
    private SoundListener listener;
    public TFAudioListener (SoundListener listener) {
        this.listener = listener;
    }

    @Override
    public void setPos (float x, float y, float z) {
        this.getListener().setPosition(x, y, z);
    }

    @Override
    public void setOrientation (Camera camera) {
        this.getListener().setOrientation(camera);
    }

    public SoundListener getListener () {
        return this.listener;
    }

    public TFAudioListener setListener (SoundListener listener) {
        this.listener = listener;
        return this;
    }
}
