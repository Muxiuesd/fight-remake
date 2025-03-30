package ttk.muxiuesd.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ttk.muxiuesd.mod.api.ModLibLoader;
import ttk.muxiuesd.mod.api.ModRegistrant;
import ttk.muxiuesd.mod.api.ModWorldProvider;
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

    private String libCode; //底层库源码
    private ScriptEngine libEngine; //运行底层库的引擎
    private boolean loaded = false;

    private ModLibManager () {
        this.libEngine = EngineFactory.createEngine();

        this.getLibEngine().put("ModLibLoader", new ModLibLoader());
        this.getLibEngine().put("ModRegistrant", new ModRegistrant());
        this.getLibEngine().put("ModWorldProvider", new ModWorldProvider());
        //this.getLibEngine().put("ModItems", new ModItems());

        Log.print(TAG, "ModLibManager 初始化完成");
    }

    /**
     * 加载mod的底层核心库
     * */
    public void loadCoreLib () {
        if (this.loaded) {
            Log.error(TAG, "核心库已经加载过，请勿重复调用加载！！！");
            return;
        }

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
