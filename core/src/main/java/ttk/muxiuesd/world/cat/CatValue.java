package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.data.JsonDataWriter;

/**
 * 用于存储cat值的包装类
 * */
public abstract class CatValue<T> implements Cloneable {
    private T value;

    public CatValue(T value) {
        this.value = value;
    }


    public T get () {
        return this.value;
    }

    public CatValue<T> set (T value) {
        this.value = value;
        return this;
    }

    /**
     * 判断cat值是否相等
     * */
    @Override
    public boolean equals (Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        CatValue<T> otherValue = (CatValue<T>) other;
        return this.get().equals(otherValue.get());
    }

    abstract public void write (String key, JsonDataWriter writer);

    abstract public void read (String key, JsonValue values);

    abstract public CatValue<T> newSelf ();

    /**
     * 重写克隆方法
     * */
    @Override
    public CatValue<T> clone () throws CloneNotSupportedException {
        super.clone();
        if (this.value instanceof CatCopyable<?> copyable){
            return this.newSelf().set((T) copyable.copySelf());
        }
        Log.error(this.getClass().getName(), "这个cat的值无法克隆！！！");
        throw new CloneNotSupportedException();
    }
}
