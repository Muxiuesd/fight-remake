package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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

    private ScriptEngine engine;

    public Mod (JsonValue info, FileHandle modDir) {
        this.info = info;
        this.modDir = modDir;
        this.modPath = modDir.path() + "/";
    }

    @Override
    public void run() {
        FileHandle mainFile = Gdx.files.local(this.modPath + info.getString("main"));
        String code = mainFile.readString();
        //Log.print(TAG, code);

        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            this.engine.eval(ModLoader.getInstance().getLib() + code);
            Invocable invocable = (Invocable) this.engine;

            this.running = true;
        } catch (ScriptException e) {
            this.running = false;
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

    public boolean isRunning () {
        return this.running;
    }
}
