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

    private boolean isDisposed () {
        return this.soundBufferSource == null;
    }

    /**
     * 数值有效性检查：NaN/Infinity 会导致 OpenAL 调用失败（AL_INVALID_VALUE），
     * 且错误是粘性的——不清除会让后续所有音频播放失败（大量播放后无声的根因）
     */
    private static boolean isInvalid (float v) {
        return Float.isNaN(v) || Float.isInfinite(v);
    }

    @Override
    public void play() {
        if (this.isDisposed()) return;
        this.soundBufferSource.play();
        this.audioEngine.addActive(this);
    }

    @Override
    public void pause() {
        if (this.isDisposed()) return;
        this.soundBufferSource.pause();
    }

    @Override
    public void resume() {
        if (this.isDisposed()) return;
        this.soundBufferSource.play();
    }

    @Override
    public void stop() {
        if (this.isDisposed()) return;
        this.soundBufferSource.stop();
    }

    @Override
    public boolean isPlaying() {
        return !this.isDisposed() && this.soundBufferSource.isPlaying();
    }

    @Override
    public void setLooping(boolean looping) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setLooping(looping);
    }

    @Override
    public void setVolume(float volume) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setVolume(volume);
    }

    @Override
    public void setPitch (float pitch) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setPitch(pitch);
    }

    @Override
    public void setPos (float x, float y, float z) {
        if (this.isDisposed()) return;
        //NaN 防护：无效坐标会触发 OpenAL 粘性错误导致后续全部无声
        if (isInvalid(x) || isInvalid(y) || isInvalid(z)) return;
        // 手动设置位置时自动解除源绑定
        this.boundSource = null;
        this.soundBufferSource.setPosition(x, y, z);
    }

    @Override
    public void setDirection (float x, float y, float z) {
        if (this.isDisposed()) return;
        //NaN 防护：无效方向会触发 OpenAL 粘性错误
        if (isInvalid(x) || isInvalid(y) || isInvalid(z)) return;
        this.soundBufferSource.setDirection(new Vector3(x, y, z));
    }

    @Override
    public void setMinAttenuation (float minDistance) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setAttenuationMinDistance(minDistance);
    }

    @Override
    public void setMaxAttenuation (float maxDistance) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setAttenuationMaxDistance(maxDistance);
    }

    @Override
    public void setAttenuationFactor (float factor) {
        if (this.isDisposed()) return;
        this.soundBufferSource.setAttenuationFactor(factor);
    }

    @Override
    public void setAttenuationEnabled (boolean enabled) {
        if (this.isDisposed()) return;
        if (enabled) this.soundBufferSource.enableAttenuation();
        else this.soundBufferSource.disableAttenuation();
    }

    @Override
    public void setBoundSource (SpatialAudioSource source) {
        if (this.isDisposed()) return;
        this.boundSource = source;
        if (source != null) {
            Vector3 pos = source.getPos();
            if (isInvalid(pos.x) || isInvalid(pos.y) || isInvalid(pos.z)) {
                //声源位置无效：跳过位置设置（防止 AL 粘性错误）
            } else {
                this.soundBufferSource.setPosition(pos.x, pos.y, pos.z);
            }
            Vector3 forward = source.getForward();
            if (isInvalid(forward.x) || isInvalid(forward.y) || isInvalid(forward.z)) {
                //声源方向无效：跳过方向设置
            } else {
                this.soundBufferSource.setDirection(forward);
            }
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
        if (this.isDisposed() || this.boundSource == null || !this.soundBufferSource.isPlaying()) return;
        Vector3 pos = this.boundSource.getPos();
        Vector3 forward = this.boundSource.getForward();
        //NaN 防护：无效位置/方向会触发 OpenAL 粘性错误导致后续全部无声
        if (isInvalid(pos.x) || isInvalid(pos.y) || isInvalid(pos.z)
            || isInvalid(forward.x) || isInvalid(forward.y) || isInvalid(forward.z)) {
            return;
        }
        this.soundBufferSource.setPosition(pos.x, pos.y, pos.z);
        this.soundBufferSource.setDirection(forward);
    }

    @Override
    public void dispose () {
        if (!this.isDisposed()) {
            this.soundBufferSource.free();
            this.soundBufferSource = null;
        }
        this.audioEngine = null;
        this.removeSpatialSource();
    }
}
