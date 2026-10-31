package ttk.muxiuesd.audio;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * 同类音效的集合，例如某个方块的全部走路音效
 * <p>
 * 组内至少有一首音效；播放时既可以按下标指定某一首，也可以用 {@link #random()} 随机取一首
 * <p>
 * 本类不实现 equals/hashCode：缓存以组对象本身做键，必须按身份区分，不能按内容合并
 * */
public class SoundGroup {
    /**
     * 快捷创建方法
     * */
    public static SoundGroup of (AudioHolder... sounds) {
        return new SoundGroup(sounds);
    }


    private final Array<AudioHolder> sounds;

    public SoundGroup (AudioHolder... sounds) {
        this(new Array<>(sounds));
    }

    public SoundGroup (Array<AudioHolder> sounds) {
        if (sounds == null || sounds.size == 0) {
            throw new IllegalArgumentException("音效组不能为空");
        }
        this.sounds = sounds;
    }

    /**
     * 按下标取某一首，下标越界属于开发期错误，这里不做兜底
     * */
    public AudioHolder get (int index) {
        return this.getSounds().get(index);
    }

    /**
     * 随机取一首
     * */
    public AudioHolder random () {
        return this.getSounds().get(MathUtils.random(this.size() - 1));
    }

    /**
     * 随机取一首，但避开传入的下标
     * @param lastIndex 上一次用过的下标，不是组内合法下标时等同于纯随机
     * */
    public AudioHolder randomExcluding (int lastIndex) {
        if (this.size() <= 1 || lastIndex < 0 || lastIndex >= this.size()) {
            return this.random();
        }
        int index = MathUtils.random(this.size() - 2);
        return this.getSounds().get(index >= lastIndex ? index + 1 : index);
    }

    /**
     * 某个音效在组内的下标，不在组内返回 -1
     * */
    public int indexOf (AudioHolder audioHolder) {
        return this.getSounds().indexOf(audioHolder, true);
    }

    /**
     * 组内音效数量
     * */
    public int size () {
        return this.getSounds().size;
    }

    public Array<AudioHolder> getSounds () {
        return this.sounds;
    }
}
