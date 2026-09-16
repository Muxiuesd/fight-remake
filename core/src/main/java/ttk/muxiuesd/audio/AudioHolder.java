package ttk.muxiuesd.audio;

import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.app.interfaces.ShallowCopyable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;

/**
 * 游戏的音效持有类
 * */
public class AudioHolder implements Codecable<AudioHolder>, ShallowCopyable<AudioHolder> {
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
    public AudioHolder copy () {
        return new AudioHolder(this.identifier).setFileHandle(this.fileHandle);
    }

    /**
     * 值语义：两个音效持有者只要 id 相同即视为相等
     * <p>
     * {@code copy()} 会生成新实例（不同的对象引用），若不覆写 equals/hashCode，
     * 两个由 {@code Item.Property.copy()} 产生的属性（如 ITEM_USE_SOUND）会被判为不等，
     * 导致 {@code ItemStack.equals} 返回 false，同种物品的堆叠无法合并。
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
