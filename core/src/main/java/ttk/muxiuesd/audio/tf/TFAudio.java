package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.math.Vector3;
import de.pottgames.tuningfork.BufferedSoundSource;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;

/**
 * TuningFork实现的立体音频
 * */
public class TFAudio implements SpatialAudio {
    private TFAudioEngine audioEngine;          //音效引擎
    private SpatialAudioSource boundSource;     //绑定的声音源
    private BufferedSoundSource soundBufferSource;    //音效缓冲

    public TFAudio (TFAudioEngine audioEngine, BufferedSoundSource soundBufferSource) {
        this.audioEngine = audioEngine;
        this.soundBufferSource = soundBufferSource;
    }

    @Override
    public void play() {
        this.soundBufferSource.play();
        this.audioEngine.addActive(this);
    }

    @Override
    public void pause() {
        this.soundBufferSource.pause();
    }

    @Override
    public void resume() {
        this.soundBufferSource.play();
    }

    @Override
    public void stop() {
        this.soundBufferSource.stop();
    }

    @Override
    public boolean isPlaying() {
        return this.soundBufferSource != null && this.soundBufferSource.isPlaying();
    }

    @Override
    public void setLooping(boolean looping) {
        this.soundBufferSource.setLooping(looping);
    }

    @Override
    public void setVolume(float volume) {
        this.soundBufferSource.setVolume(volume);
    }

    @Override
    public void setPitch (float pitch) {
        this.soundBufferSource.setPitch(pitch);
    }

    @Override
    public void setPos (float x, float y, float z) {
        // 手动设置位置时自动解除源绑定
        this.boundSource = null;
        this.soundBufferSource.setPosition(x, y, z);
    }

    @Override
    public void setDirection (float x, float y, float z) {
        this.soundBufferSource.setDirection(new Vector3(x, y, z));
    }

    @Override
    public void setMinAttenuation (float minDistance) {
        this.soundBufferSource.setAttenuationMinDistance(minDistance);
    }

    @Override
    public void setMaxAttenuation (float maxDistance) {
        this.soundBufferSource.setAttenuationMaxDistance(maxDistance);
    }

    @Override
    public void setAttenuationFactor (float factor) {
        this.soundBufferSource.setAttenuationFactor(factor);
    }

    @Override
    public void setAttenuationEnabled (boolean enabled) {
        if (enabled) this.soundBufferSource.enableAttenuation();
        else this.soundBufferSource.disableAttenuation();
    }

    @Override
    public void setBoundSource (SpatialAudioSource source) {
        this.boundSource = source;
        // 立即同步一次位置与朝向
        if (source != null) {
            Vector3 pos = source.getPos();
            this.soundBufferSource.setPosition(pos.x, pos.y, pos.z);
            this.soundBufferSource.setDirection(source.getForward());
        }
    }

    @Override
    public SpatialAudioSource getBoundSource () {
        return this.boundSource;
    }

    @Override
    public void removeSpatialSource () {
        this.boundSource = null;
    }

    @Override
    public void updatePos () {
        if (this.boundSource != null && this.soundBufferSource != null && this.soundBufferSource.isPlaying()) {
            Vector3 pos = this.boundSource.getPos();
            this.soundBufferSource.setPosition(pos.x, pos.y, pos.z);
            this.soundBufferSource.setDirection(this.boundSource.getForward());
        }
    }

    @Override
    public void dispose () {
        if (this.soundBufferSource != null) {
            this.soundBufferSource.free();
            this.soundBufferSource = null;
        }
        this.audioEngine = null;
        this.removeSpatialSource();
    }
}
