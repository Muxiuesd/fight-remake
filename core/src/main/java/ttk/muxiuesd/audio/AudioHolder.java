package ttk.muxiuesd.audio;

import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.id.Identifier;

/**
 * 游戏的音效持有类
 * */
public class AudioHolder {
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
}
