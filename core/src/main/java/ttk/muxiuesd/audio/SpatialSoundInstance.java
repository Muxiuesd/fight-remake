package ttk.muxiuesd.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 空间音效实例
 * 为了控制音效播放以及检测播放状态，迫不得已改为 {@link Music}
 * */
public class SpatialSoundInstance extends MusicInstance implements Updateable {
    private final Entity<?> sounder;   //发声者
    private final Entity<?> receiver;  //接收者

    private boolean isFront = true; //是否在前方，在后方的音量会较小

    public SpatialSoundInstance (Music music, Entity<?> sounder, Entity<?> receiver) {
        super(music);
        this.sounder = sounder;
        this.receiver = receiver;
    }

    @Override
    public void update (float delta) {
        float volume = this.calculateVolume();
        Music music = getMusic();
        if (volume > 0f) {
            float pan = this.calculatePan();
            if (!this.isFront) {
                volume *= 0.7f;
            }
            music.setPan(pan, volume);
            if (!isPlaying()) {
                getMusic().play();
            }
        }
    }

    /**
     * 计算音量
     * 距离越近自然音量越大
     * */
    public float calculateVolume() {
        float distance = receiver.getPosition().dst(sounder.getPosition());
        Float hearingRange = Fight.PLAYER_HEARING_RANGE.getValue();
        if (distance > hearingRange) {
            return 0f;
        }
        return 1f - distance / hearingRange;
    }

    /**
     * 计算方位
     * 通过方位控制左右声道音量大小
     * */
    public float calculatePan () {
        Vector2 sp = this.sounder.getPosition();
        Vector2 rp = this.receiver.getPosition();
        //TODO 目前先这么写吧
        float deg = MathUtils.atan2Deg360(sp.y - rp.y, sp.x - rp.x);
        float v = MathUtils.cosDeg(deg);
        //判断前后
        this.isFront = !this.inRange(deg, 240f, 360f);
        return inRange(v, -0.1f, 0.1f)? v : 0;
    }

    private boolean inRange(float value, float start, float end) {
        return value > start && value < end;
    }
}
