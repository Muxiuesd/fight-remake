package ttk.muxiuesd.audio;

import com.badlogic.gdx.audio.Sound;

/**
 * 正在播放的音效的实例
 * */
public class SoundInstance {
    //定义sound播放模式
    public static final int PLAY    = 0;
    public static final int LOOPING = 1;

    private final Sound sound;
    private long id;

    public SoundInstance(Sound sound) {
        this.sound = sound;
    }
    public SoundInstance(Sound sound, int mode) {
        this(sound, mode, 1f, 1f, 0f);
    }

    public SoundInstance(Sound sound, int mode, float volume, float pitch, float pan) {
        this.sound = sound;
        if (mode == PLAY) {
            this.id = this.sound.play(volume, pitch, pan);
        }
        if (mode == LOOPING) {
            this.id = this.sound.loop(volume, pitch, pan);
        }
    }

    public void play () {
        this.id = this.sound.play();
    }

    public void stop () {
        this.sound.stop(this.id);
    }

    public void pause () {
        this.sound.pause(this.id);
    }

    public void resume () {
        this.sound.resume(this.id);
    }

    public Sound getSound () {
        return sound;
    }

    public long getId () {
        return id;
    }
}
