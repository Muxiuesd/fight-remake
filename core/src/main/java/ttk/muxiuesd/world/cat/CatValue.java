package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;

/**
 * 用于存储cat值的包装类
 * */
public abstract class CatValue<T> implements Cloneable{
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
     * <p>
     * 基本类型值（CatInt/CatFloat/CatBoolean/CatString/CatLong 等，值非 {@link CatCopyable}）
     * 直接用 {@link #newSelf()} 值复制（不再抛异常）；复杂对象值走 CatCopyable 深拷贝
     * */
    @Override
    public CatValue<T> clone () {
        if (this.value instanceof CatCopyable<?> copyable){
            return this.newSelf().set((T) copyable.copySelf());
        }
        return this.newSelf();
    }
}
