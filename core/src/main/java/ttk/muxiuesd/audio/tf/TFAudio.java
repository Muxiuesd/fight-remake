package ttk.muxiuesd.audio.tf;

import com.badlogic.gdx.math.Vector3;
import de.pottgames.tuningfork.BufferedSoundSource;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudioSource;

/**
 * TuningFork实现的立体音频
 * */
public class TFAudio implements SpatialAudio {
    private SpatialAudioSource boundSource;     //绑定的声音源
    private BufferedSoundSource soundBuffer;

    public TFAudio (BufferedSoundSource soundBuffer) {
        this.soundBuffer = soundBuffer;
    }

    @Override
    public void play() {
        this.soundBuffer.play();
    }

    @Override
    public void pause() {
        this.soundBuffer.pause();
    }

    @Override
    public void resume() {
        //没有
    }

    @Override
    public void stop() {
        this.soundBuffer.stop();
    }

    @Override
    public boolean isPlaying() {
        return this.soundBuffer.isPlaying();
    }

    @Override
    public void setLooping(boolean looping) {
        this.soundBuffer.setLooping(looping);
    }

    @Override
    public void setVolume(float volume) {
        this.soundBuffer.setVolume(volume);
    }

    @Override
    public void setPitch (float pitch) {
        this.soundBuffer.setPitch(pitch);
    }

    @Override
    public void setPos (float x, float y, float z) {
        // 手动设置位置时自动解除源绑定
        this.boundSource = null;
        this.soundBuffer.setPosition(x, y, z);
    }

    @Override
    public void setDirection (float x, float y, float z) {
        this.soundBuffer.setDirection(new Vector3(x, y, z));
    }

    @Override
    public void setAttenuation (float maxDistance) {
        this.soundBuffer.setAttenuationMaxDistance(maxDistance);
    }

    @Override
    public void setSpatialSource (SpatialAudioSource source) {
        this.boundSource = source;
        // 立即同步一次位置与朝向
        if (source != null) {
            Vector3 pos = source.getPos();
            this.soundBuffer.setPosition(pos.x, pos.y, pos.z);
            this.soundBuffer.setDirection(source.getForward());
        }
    }

    @Override
    public void removeSpatialSource () {
        this.boundSource = null;
    }

    @Override
    public void updatePos () {
        if (this.boundSource != null && this.soundBuffer.isPlaying()) {
            Vector3 pos = this.boundSource.getPos();
            this.soundBuffer.setPosition(pos.x, pos.y, pos.z);
            this.soundBuffer.setDirection(this.boundSource.getForward());
        }
    }

    @Override
    public void dispose () {
        this.soundBuffer.free();
    }

    public SpatialAudioSource getBoundSource () {
        return boundSource;
    }

    public TFAudio setBoundSource (SpatialAudioSource boundSource) {
        this.boundSource = boundSource;
        return this;
    }
}
