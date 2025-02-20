package ttk.muxiuesd.audio;

import com.badlogic.gdx.audio.Music;

/**
 * 播放中的音乐实例
 * */
public class MusicInstance implements Music{
    private final Music music;


    public MusicInstance (Music music, PlayMode mode, float volume, float pan) {
        this(music);
        this.music.setPan(pan, volume);
        if (mode == PlayMode.PLAY) {
            this.music.play();
        }else if (mode == PlayMode.LOOP) {
            this.music.setLooping(true);
        }
    }
    public MusicInstance (Music music) {
        this.music = music;
    }

    @Override
    public void play () {
        this.music.play();
    }

    @Override
    public void pause () {
        this.music.pause();
    }

    @Override
    public void stop () {
        this.music.stop();
    }

    @Override
    public boolean isPlaying () {
        return this.music.isPlaying();
    }

    @Override
    public void setLooping (boolean isLooping) {
        this.music.setLooping(isLooping);
    }

    @Override
    public boolean isLooping () {
        return this.music.isLooping();
    }

    @Override
    public void setVolume (float volume) {
        this.music.setVolume(volume);
    }

    @Override
    public float getVolume () {
        return this.music.getVolume();
    }

    @Override
    public void setPan (float pan, float volume) {
        this.music.setPan(pan, volume);
    }

    @Override
    public void setPosition (float position) {
        this.music.setPosition(position);
    }

    @Override
    public float getPosition () {
        return this.music.getPosition();
    }

    @Override
    public void dispose () {
        this.music.dispose();
    }

    public Music getMusic () {
        return this.music;
    }

    @Override
    public void setOnCompletionListener (OnCompletionListener listener) {
        this.music.setOnCompletionListener(listener);
    }
}
