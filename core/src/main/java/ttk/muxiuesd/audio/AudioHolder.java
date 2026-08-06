package ttk.muxiuesd.audio;

import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;

/**
 * 游戏的音效持有类
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

    @Override
    public Codec<AudioHolder> getCodec () {
        return CODEC;
    }
}
