package ttk.muxiuesd.mod.api;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.function.Supplier;

/**
 * modAPI：模组注册时的提供类
 * */
public abstract class ModSupplier<T> implements Supplier<T> {
    public abstract ScriptObjectMirror getNew ();

    @Override
    public T get () {
        ScriptObjectMirror objectMirror = this.getNew();
        Object object = objectMirror.call("");
        return (T) object;
    }
}
