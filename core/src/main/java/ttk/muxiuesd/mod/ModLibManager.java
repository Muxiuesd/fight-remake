package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.mod.api.ModLibLoader;
import ttk.muxiuesd.util.Log;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * mod底层库的加载、管理
 * <p>
 * 应该仅限{@link ModLoader}、{@link ModLibLoader}使用
 * */
public class ModLibManager {
    public final String TAG = this.getClass().getName();
    //单例模式
    private static ModLibManager Instance;

    private String libCode;
    private ScriptEngine libEngine;

    private ModLibManager () {
        this.libEngine = EngineFactory.createEngine();

        this.getLibEngine().put("ModLibLoader", new ModLibLoader());

        Log.print(TAG, "ModLibManager 初始化完成");
    }

    /**
     * 加载mod的底层库
     * */
    protected void loadAllLib () {
        FileHandle libFile = Gdx.files.internal("modlib/lib.js");
        this.libCode = libFile.readString();
        try {
            this.libEngine.eval(this.getLibCode());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        Log.print(TAG, "模组底层库加载完毕。");
    }

    /**
     * 获取实例
     * */
    public static ModLibManager getInstance () {
        if (Instance == null) {
            Instance = new ModLibManager();
        }
        return Instance;
    }

    /**
    * 获取底层库的代码
    * */
    public String getLibCode () {
        return this.libCode;
    }

    public ScriptEngine getLibEngine () {
        return this.libEngine;
    }
}
