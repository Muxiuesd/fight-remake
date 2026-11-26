package ttk.muxiuesd.audio;

import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;

/**
 * 游戏的音效持有类
 * <p>
 * 无状态引用：只持有音效 id（{@link Identifier}）与文件句柄（{@link FileHandle}），
 * 没有需要隔离的可变数据。因此<b>不实现 {@code ShallowCopyable}</b>——
 * 在复制属性时直接共享引用，而非生成无意义的新实例副本
 * */
public class AudioHolder implements Codecable<AudioHolder> {
    public static final Codec<AudioHolder> CODEC = Codec.STRING.xmap(
        Registries.AUDIOS::get,
        AudioHolder::getID
        );


    private final Identifier identifier;    //音效的id包装类
    private FileHandle fileHandle;          //音效的文件路径

    public AudioHolder (Identifier identifier){
        this.identifier = identifier;
    }

    /**
     * 获取持有的id字符串
     * */
    public String getID () {
        return this.identifier.getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public FileHandle getFileHandle () {
        return this.fileHandle;
    }

    public AudioHolder setFileHandle (FileHandle filehandle) {
        this.fileHandle = filehandle;
        return this;
    }

    /**
     * 值语义：两个音效持有者只要 id 相同即视为相等
     * <p>
     * 属性复制时共享同一实例（不实现 ShallowCopyable），引用相等即可；此处覆写相等判定仅作健壮性，
     * 防止未来出现同 id 不同实例时被误判不等
     */
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AudioHolder other)) return false;
        return this.identifier.equals(other.identifier);
    }

    @Override
    public int hashCode () {
        return this.identifier.hashCode();
    }

    @Override
    public Codec<AudioHolder> getCodec () {
        return CODEC;
    }
}
