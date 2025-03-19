package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.util.Log;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * 一个mod
 * */
public class Mod implements Runnable{
    public final String TAG = this.getClass().getName();

    private boolean running = false;
    private JsonValue info;
    private FileHandle modDir;
    private String modPath;

    private ScriptContext libContext;   //mod底层库的上下文
    private ScriptEngine engine;

    public Mod (JsonValue info, FileHandle modDir, ScriptContext libContext) {
        this.info = info;
        this.modDir = modDir;
        this.modPath = modDir.path() + "/";
        this.libContext = libContext;
    }

    @Override
    public void run() {
        FileHandle mainFile = Gdx.files.absolute(this.modPath + info.getString("main"));

        String code = mainFile.readString();
        //Log.print(TAG, code);

        this.engine = EngineFactory.createEngine();
        try {

            this.engine.eval(code, this.libContext);

            this.running = true;
        } catch (ScriptException e) {
            this.running = false;
            Log.error(TAG, "Mod：" + this.getModName() + " 出bug了，运行失败！！！");
            throw new RuntimeException(e);
        }
    }

    public ScriptEngine getEngine () {
        if (this.engine == null) {
            Log.error(TAG, "在不合适的时候获取mod的引擎！");
            return null;
        }
        return this.engine;
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

    /**
     * 获取mod文件夹的路径
     * */
    public String getModPath() {
        return this.modPath;
    }

    public boolean isRunning () {
        return this.running;
    }
}
