package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;

import ttk.muxiuesd.util.Log;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
/**
 * 一个mod
 * */
public class Mod implements Runnable{
    public final String TAG = this.getClass().getName();

    private JsonValue info;

    private FileHandle modDir;
    private String modPath;

    public Mod (JsonValue info, FileHandle modDir) {
        this.info = info;
        this.modDir = modDir;
        this.modPath = modDir.path() + "/";
    }

    @Override
    public void run() {
        //加载底层库
        FileHandle libFile = Gdx.files.internal("lib/lib.js");
        String lib = libFile.readString();
        //Log.print(TAG, lib);

        FileHandle mainFile = Gdx.files.local(this.modPath + info.getString("main"));
        String code = mainFile.readString();
        //Log.print(TAG, code);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(lib + code);
            Invocable invocable = (Invocable) engine;

            //Log.print(TAG, lib + code);

        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public String getModName() {
        return this.info.get("name").asString();
    }

    /**
     * mod的名字可以重复，但是命名空间不可重复
     * @return
     */
    public String getModNamespace() {
        return this.info.get("namespace").asString();
    }
}
